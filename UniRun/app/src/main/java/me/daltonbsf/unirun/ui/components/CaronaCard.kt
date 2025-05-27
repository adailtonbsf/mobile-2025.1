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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CaronaCard(carona: Carona, isUserInCarona: Boolean) {
    val backgroundColor = if (isUserInCarona) {
        MaterialTheme.colorScheme.primaryContainer // Cor quando o usuário está na carona
    } else {
        Color.Transparent // Cor padrão ou outra cor desejada
    }

    Row(
        modifier = Modifier
            .background(backgroundColor) // Define o background aqui
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Image(
            painterResource(R.drawable.car_loc_icon),
            contentDescription = "Carona Icon",
            Modifier.size(64.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                "${carona.origin} ➜ ${carona.destiny} (${carona.creator})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )

            val dateTime = carona.departureDate
            val today = LocalDate.now()
            val departureDate = dateTime.toLocalDate()

            val datePart = when {
                departureDate.isEqual(today) -> "Hoje"
                departureDate.isEqual(today.plusDays(1)) -> "Amanhã"
                else -> departureDate.format(DateTimeFormatter.ofPattern("dd/MM"))
            }
            val timePart = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            val departureDateTimeText = "$datePart, $timePart"

            Text(departureDateTimeText)
        }
    }
}