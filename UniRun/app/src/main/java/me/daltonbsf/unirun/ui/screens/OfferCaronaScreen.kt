package me.daltonbsf.unirun.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.MutableState
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
import androidx.navigation.NavController
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.util.CaronaAlarmReceiver
import me.daltonbsf.unirun.util.NotificationUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
@RequiresPermission(
    allOf = [
        Manifest.permission.SCHEDULE_EXACT_ALARM,
        Manifest.permission.POST_NOTIFICATIONS
    ]
)
fun OfferCaronaScreen(navController: NavController) {
    val context = LocalContext.current
    val caronaTimeMillis = remember { mutableLongStateOf(System.currentTimeMillis()) }

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
                    navController.popBackStack()
                    val oneHourBefore = caronaTimeMillis.longValue - 60 * 60 * 1000
                    if (System.currentTimeMillis() >= oneHourBefore) {
                        // Se já passou do horário, mostra a notificação imediatamente
                        NotificationUtils.showNotification(
                            context = context,
                            title = "Carona Agendada",
                            text = "Sua carona sairá em menos 1 hora!"
                        )
                    } else {
                        // Caso contrário, agenda a notificação para 1 hora antes
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(context, CaronaAlarmReceiver::class.java).apply {
                            putExtra("title", "Carona Agendada")
                            putExtra("text", "Sua carona sairá em menos 1 hora!")
                        }
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            oneHourBefore,
                            pendingIntent
                        )
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
            DateTimePicker(context, caronaTimeMillis)
        }
    }
}

@Composable
fun DateTimePicker(context: Context, caronaTimeMillis: MutableState<Long>) {
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    var selectedDateTimeText by remember { mutableStateOf(dateFormat.format(calendar.time)) }

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
                    selectedDateTimeText = dateFormat.format(calendar.time)
                    caronaTimeMillis.value = calendar.timeInMillis // Atualiza o estado compartilhado
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