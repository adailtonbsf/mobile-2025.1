package me.daltonbsf.unirun.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.data.UserPreferences
import me.daltonbsf.unirun.util.CaronaReminderReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun OfferCaronaScreen(navController: NavController) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    var selectedDateTimeMillis by remember { mutableLongStateOf(calendar.timeInMillis) }
    var selectedDateTimeText by remember { mutableStateOf(dateFormat.format(calendar.time)) }
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
                onClick = {
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
                                navController.popBackStack()
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
                                val reminderTimeMillis = selectedDateTimeMillis - 60 * 60 * 1000 // 1h antes

                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    reminderTimeMillis,
                                    pendingIntent
                                )
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
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
        Column(modifier = Modifier.padding(innerPadding)) {
            Image(
                painter = painterResource(R.drawable.map_background),
                contentDescription = "Map Background",
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
            )
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