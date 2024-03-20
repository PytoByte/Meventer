package pachmp.meventer.components.mainmenu.components.profile.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun Avatar(model: String) {
    AsyncImage(
        model = model,
        contentDescription = "avatar",
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}