package pachmp.meventer.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import pachmp.meventer.R
import pachmp.meventer.components.NavGraph
import pachmp.meventer.components.NavGraphs

enum class BottomBarScreens (
    val navGraph: NavGraph,
    val titleResourseID: Int,
    val icon: ImageVector
){
    Profile(
        navGraph = NavGraphs.profile,
        titleResourseID = R.string.profile,
        icon = Icons.Default.Person
    ),
    Events(
        navGraph = NavGraphs.events,
        titleResourseID = R.string.events,
        icon = Icons.Default.Search
    ),
    Chats(
        navGraph = NavGraphs.chats,
        titleResourseID = R.string.chats,
        icon = Icons.Default.ChatBubble
    )
}