package pachmp.meventer.components.mainmenu.components.profile.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.components.widgets.Background
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.LocalDate

@ProfileNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Edit button
                IconButton(
                    onClick = { profileViewModel.navigateToEdit() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit account",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Logout button
                IconButton(
                    onClick = { profileViewModel.logout() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Background()
        if (profileViewModel.user != null) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                // Avatar
                AsyncImage(modifier=Modifier.size(150.dp).clip(CircleShape), model = profileViewModel.avatar, contentDescription = "avatar", contentScale = ContentScale.Crop)

                // Username and ID
                Spacer(modifier = Modifier.height(12.dp))
                Text("Username", style = MaterialTheme.typography.headlineLarge)
                ProfileInfo(rating = 2.3f) // TODO: We dont have this?

                // Details
                Spacer(modifier = Modifier.height(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileDetail("email", profileViewModel.user!!.email)
                    ProfileDetail("phone", "in progress") // TODO: We dont have this
                    ProfileDetail(
                        "age",
                        (LocalDate.now().year - profileViewModel.user!!.dateOfBirth.year).toString()
                    )
                    ProfileDetail("sex?", "in progress") // TODO: We dont have this
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Загрузка")
            }
        }
    }
}


@Composable
fun ProfileDetail(label: String, value: String) {
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5f.dp, Color.Black),
        modifier = Modifier.height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                maxLines = 1
            )
            Text(
                value, modifier = Modifier
                    .weight(3f)
                    .horizontalScroll(rememberScrollState()),
                fontSize = 16.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun Avatar(image: Painter) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape)
    )
}

@Composable
fun ProfileInfo(rating: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(300.dp)
    ) {
        RatingBar(
            value = rating,
            config = RatingBarConfig().numStars(5).style(RatingBarStyle.HighLighted).size(30.dp),
            onValueChange = {},
            onRatingChanged = {})
        Text(text = "Rating: $rating", style = MaterialTheme.typography.bodyLarge)
    }
}