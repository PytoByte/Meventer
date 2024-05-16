package pachmp.meventer.components.mainmenu.components.events.components.display

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import pachmp.meventer.data.enums.Ranks

data class UserModel(
    val id: Int,
    val avatar: String?,
    val name: String?,
    val ranks: Ranks,
    var image: MutableState<ImageBitmap>? = null
)
