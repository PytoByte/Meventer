package pachmp.meventer.components.mainmenu.components.events

import com.ramcosta.composedestinations.spec.Direction
import pachmp.meventer.components.destinations.DirectionDestination
import pachmp.meventer.components.destinations.EditEventScreenDestination
import pachmp.meventer.components.destinations.EventScreenDestination

enum class EventScreens(val direction: DirectionDestination) {
    EDIT(EditEventScreenDestination),
    INFO(EventScreenDestination)
}