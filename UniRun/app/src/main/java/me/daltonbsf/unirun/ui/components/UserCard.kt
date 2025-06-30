package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.R

@Composable
fun UserCard(user: User, navController: NavController) {
    Card(
        onClick = { navController.navigate("peopleChat/${user.name}") }
    ){
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.profileImageURL,
                    contentDescription = "Profile Image",
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.error),
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    user.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Usuário(a) desde: ${user.registrationDate} \n" +
                        "Caronas oferecidas: ${user.offeredRidesCount} \n" +
                        "Caronas solicitadas: ${user.requestedRidesCount}" +
                        if (user.bio != "") "\n\nBio: ${user.bio}" else "",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp, end = 16.dp),
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}