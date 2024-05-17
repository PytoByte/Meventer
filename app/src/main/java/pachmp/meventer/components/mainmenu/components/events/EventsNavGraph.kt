package pachmp.meventer.components.mainmenu.components.events

import com.ramcosta.composedestinations.annotation.NavGraph
import pachmp.meventer.components.mainmenu.BottomNavGraph

@BottomNavGraph(start = true)
@NavGraph
annotation class EventsNavGraph(
    val start: Boolean = false
)