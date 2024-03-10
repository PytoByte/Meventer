package pachmp.meventer.components.mainmenu.components

import com.ramcosta.composedestinations.annotation.NavGraph
import pachmp.meventer.components.mainmenu.screens.MainmenuNavGraph

@MainmenuNavGraph
@NavGraph
annotation class BottomNavGraph(
    val start: Boolean = false
)