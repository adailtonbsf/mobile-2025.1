package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
//import me.daltonbsf.unirun.model.caronaList
import me.daltonbsf.unirun.ui.components.CaronaCard

@Composable
fun CaronaScreen(navController: NavController) {
    Column {
        Text(
            "Caronas",
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Button(
            onClick = { navController.navigate("offerCarona") },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                "Oferecer Carona",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(8.dp)
            )
        }
        Card(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .clickable {
                    navController.navigate("caronaDetails/1")
                }
        ) {
            /*CaronaCard( TODO: Implementar o CaronaRepository
                caronaList[0], false
            )*/
        }
        Card(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .clickable {
                    navController.navigate("caronaDetails/2")
                }
        ) {
            /*CaronaCard( TODO: Implementar o CaronaRepository
                caronaList[1], true
            )*/
        }
    }
}