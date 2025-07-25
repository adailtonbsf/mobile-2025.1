package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.model.Carona
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CaronaCard(carona: Carona, isUserInCarona: Boolean) {
    val backgroundColor = if (isUserInCarona) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.car_loc_icon),
            contentDescription = "Carona Icon",
            Modifier.size(64.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                "${carona.originName} ➜ ${carona.destinyName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )

            val departureMillis = carona.departureDate.toLongOrNull()
            val departureDateTimeText = if (departureMillis != null) {
                val instant = Instant.ofEpochMilli(departureMillis)
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val today = LocalDate.now()
                val departureDate = dateTime.toLocalDate()

                val datePart = when {
                    departureDate.isEqual(today) -> "Hoje"
                    departureDate.isEqual(today.plusDays(1)) -> "Amanhã"
                    else -> departureDate.format(DateTimeFormatter.ofPattern("dd/MM"))
                }
                val timePart = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                "$datePart, $timePart"
            } else {
                "Data inválida"
            }

            Text(departureDateTimeText, style = MaterialTheme.typography.bodyMedium)

            if (carona.information.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Informação",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = carona.information,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .basicMarquee()
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Assentos",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${carona.seatsAvailable}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (carona.seatsAvailable > 1) "vagas" else "vaga",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}