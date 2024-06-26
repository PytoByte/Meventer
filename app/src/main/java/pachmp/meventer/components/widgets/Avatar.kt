package pachmp.meventer.components.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun Avatar(imageBitmap: ImageBitmap) {
    Image(
        bitmap = imageBitmap,
        contentDescription = "avatar",
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}