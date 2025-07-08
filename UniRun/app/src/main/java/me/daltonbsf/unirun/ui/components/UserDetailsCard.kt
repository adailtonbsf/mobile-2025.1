package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.R

@Composable
fun UserDetailsCard(user: User, navController: NavController) {
    Card (
        modifier = Modifier
            .clickable(onClick = { navController.navigate("peopleChat/${user.name}") })
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.profileImageURL,
                contentDescription = "Profile Image",
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.error),
                modifier = Modifier
                    .padding(8.dp)
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (user.bio != "") user.bio
                    else "Usuário(a) desde: ${user.registrationDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}