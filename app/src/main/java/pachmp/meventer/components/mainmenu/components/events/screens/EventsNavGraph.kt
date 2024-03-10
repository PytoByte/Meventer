package pachmp.meventer.components.mainmenu.components.events.screens

import com.ramcosta.composedestinations.annotation.NavGraph
import pachmp.meventer.components.mainmenu.components.BottomNavGraph

@BottomNavGraph(start = true)
@NavGraph
annotation class EventsNavGraph(
    val start: Boolean = false
)