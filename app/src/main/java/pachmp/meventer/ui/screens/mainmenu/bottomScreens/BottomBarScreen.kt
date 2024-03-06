package pachmp.meventer.ui.screens.mainmenu.bottomScreens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen (
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Profile: BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Settings
    )
    object AllEvents: BottomBarScreen(
        route = "allevents",
        title = "All Events",
        icon = Icons.Default.Search
    )
    object YourEvents: BottomBarScreen(
        route = "yourevents",
        title = "Your Events",
        icon = Icons.Default.AccountCircle
    )
}