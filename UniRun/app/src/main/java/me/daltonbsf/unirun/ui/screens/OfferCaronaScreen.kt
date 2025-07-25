package me.daltonbsf.unirun.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.daltonbsf.unirun.data.UserPreferences
import me.daltonbsf.unirun.util.CaronaReminderReceiver
import me.daltonbsf.unirun.viewmodel.CaronaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class LocationField {
    START, DESTINATION
}

@SuppressLint("ObsoleteSdkInt")
@ExperimentalMaterial3Api
@Composable
@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun OfferCaronaScreen(navController: NavController, caronaViewModel: CaronaViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    var selectedDateTimeMillis by remember { mutableLongStateOf(calendar.timeInMillis) }
    var selectedDateTimeText by remember { mutableStateOf(dateFormat.format(calendar.time)) }

    var showDetailsDialog by remember { mutableStateOf(false) }


    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var hasLocationPermission by remember {
        mutableStateOf(
            locationPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.values.all { it }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    val UFC = LatLng(-4.979089750971326, -39.056514252078195)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UFC, 12f)
    }

    // --- Lógica do Places API ---
    var startSearchQuery by remember { mutableStateOf("") }
    var destSearchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<FindAutocompletePredictionsResponse?>(null) }
    var startSelectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var destSelectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val placesClient = remember { Places.createClient(context) }
    var activeField by remember { mutableStateOf<LocationField?>(null) }

    LaunchedEffect(startSearchQuery, destSearchQuery) {
        val query = when (activeField) {
            LocationField.START -> startSearchQuery
            LocationField.DESTINATION -> destSearchQuery
            null -> ""
        }

        if (query.isNotEmpty()) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()
            try {
                predictions = placesClient.findAutocompletePredictions(request).await()
            } catch (e: Exception) {
                Log.e("PlacesAPI", "Erro ao buscar previsões", e)
            }
        } else {
            predictions = null
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    }
                }
            } catch (e: SecurityException) {
                Log.e("OfferCaronaScreen", "Erro ao obter localização: ${e.message}")
            }
        }
    }

    if (showDetailsDialog) {
        CaronaDetailsDialog(
            onDismiss = { showDetailsDialog = false },
            onConfirm = { seats, additionalInfo ->
                showDetailsDialog = false
                caronaViewModel.createCarona(
                    startLocation = startSelectedLocation,
                    destLocation = destSelectedLocation,
                    startLocationName = startSearchQuery,
                    destLocationName = destSearchQuery,
                    dateTimeMillis = selectedDateTimeMillis,
                    seats = seats,
                    additionalInfo = additionalInfo
                )
                val userPreferences = UserPreferences.getInstance(context)
                CoroutineScope(Dispatchers.Main).launch {
                    val allNotificationsEnabled = userPreferences
                        .getPreference(UserPreferences.ALL_NOTIFICATIONS_KEY, "true")
                        .first() == "true"
                    val rideLeavingEnabled = userPreferences.getPreference(UserPreferences.RIDE_LEAVING_NOTIFICATIONS_KEY, "true")
                        .first() == "true"

                    if (allNotificationsEnabled && rideLeavingEnabled) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = ("package:" + context.packageName).toUri()
                            }
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, CaronaReminderReceiver::class.java).apply {
                                putExtra("title", "Carona agendada")
                                putExtra("message", "Sua carona sairá em menos de 1 hora!")
                            }
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            val reminderTimeMillis = selectedDateTimeMillis - 60 * 60 * 1000

                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                reminderTimeMillis,
                                pendingIntent
                            )
                        }
                    }
                }
                navController.popBackStack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Oferecer Carona",
                        modifier = Modifier.padding(8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { showDetailsDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = startSelectedLocation != null && destSelectedLocation != null && startSearchQuery.isNotBlank() && destSearchQuery.isNotBlank()
            ) {
                Text(
                    "Confirmar Carona",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                    onMapClick = { latLng ->
                        when (activeField) {
                            LocationField.START -> startSelectedLocation = latLng
                            LocationField.DESTINATION -> destSelectedLocation = latLng
                            null -> {}
                        }
                    }
                ) {
                    startSelectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Ponto de Partida"
                        )
                    }
                    destSelectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Destino"
                        )
                    }
                }
                Column(modifier = Modifier.padding(8.dp)) {
                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = startSearchQuery,
                        onValueChange = { startSearchQuery = it },
                        label = { Text("Ponto de Partida") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) {
                                    activeField = LocationField.START
                                }
                            },
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = destSearchQuery,
                        onValueChange = { destSearchQuery = it },
                        label = { Text("Destino") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) {
                                    activeField = LocationField.DESTINATION
                                }
                            },
                        colors = textFieldColors
                    )

                    if (activeField != null) {
                        predictions?.let {
                            LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                                items(it.autocompletePredictions) { prediction ->
                                    Text(
                                        text = prediction.getFullText(null).toString(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val placeFields =
                                                    listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                                                val request = FetchPlaceRequest.builder(
                                                    prediction.placeId,
                                                    placeFields
                                                ).build()
                                                placesClient.fetchPlace(request)
                                                    .addOnSuccessListener { response ->
                                                        val place = response.place
                                                        val location = place.latLng
                                                        val name = place.name ?: ""

                                                        when (activeField) {
                                                            LocationField.START -> {
                                                                startSelectedLocation = location
                                                                startSearchQuery = name
                                                            }

                                                            LocationField.DESTINATION -> {
                                                                destSelectedLocation = location
                                                                destSearchQuery = name
                                                            }

                                                            null -> {}
                                                        }

                                                        predictions = null
                                                        activeField = null
                                                        location?.let { latLng ->
                                                            cameraPositionState.move(
                                                                CameraUpdateFactory.newLatLngZoom(
                                                                    latLng,
                                                                    15f
                                                                )
                                                            )
                                                        }
                                                        focusManager.clearFocus()
                                                    }
                                            }
                                            .padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            DateTimePicker(
                selectedDateTimeText = selectedDateTimeText,
                onDateTimeSelected = { millis, text ->
                    selectedDateTimeMillis = millis
                    selectedDateTimeText = text
                }
            )
        }
    }
}

@Composable
fun CaronaDetailsDialog(
    onDismiss: () -> Unit,
    onConfirm: (seats: Int, additionalInfo: String) -> Unit
) {
    var seats by remember { mutableStateOf("4") }
    var additionalInfo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalhes da Carona") },
        text = {
            Column {
                OutlinedTextField(
                    value = seats,
                    onValueChange = { seats = it.filter { char -> char.isDigit() } },
                    label = { Text("Assentos disponíveis") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = additionalInfo,
                    onValueChange = { additionalInfo = it },
                    label = { Text("Informação adicional (opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val numSeats = seats.toIntOrNull() ?: 0
                    if (numSeats > 0) {
                        onConfirm(numSeats, additionalInfo)
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DateTimePicker(
    selectedDateTimeText: String,
    onDateTimeSelected: (Long, String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val text = dateFormat.format(calendar.time)
                    onDateTimeSelected(calendar.timeInMillis, text)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val isDarkTheme = isSystemInDarkTheme()
    val textFieldColors = if (isDarkTheme) {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.White,
            disabledBorderColor = Color.Gray,
            disabledLeadingIconColor = Color.LightGray,
            disabledTrailingIconColor = Color.LightGray,
            disabledLabelColor = Color.LightGray,
            disabledPlaceholderColor = Color.DarkGray,
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    OutlinedTextField(
        value = selectedDateTimeText,
        onValueChange = { },
        label = { Text("Selecionar Data e Hora") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { datePickerDialog.show() },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Selecionar Data e Hora",
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        },
        readOnly = true,
        enabled = false,
        colors = textFieldColors
    )
}