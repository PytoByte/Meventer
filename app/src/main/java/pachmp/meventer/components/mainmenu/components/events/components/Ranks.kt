package pachmp.meventer.components.mainmenu.components.events.components

import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.User

enum class Rank(val value: Byte, val title: String) {
    PARTICIPANT(0.toByte(), "Участник"),
    ORGANIZER(1.toByte(), "Организатор"),
    ORIGINATOR(2.toByte(), "Основатель")
}

fun getUserRank(event: Event, user: User) = when (user.id) {
    event.originator -> Rank.ORIGINATOR
    in event.organizers -> Rank.ORGANIZER
    else -> Rank.PARTICIPANT
}