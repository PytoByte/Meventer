package pachmp.meventer.data.enums

import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.User

enum class Ranks(val value: Byte, val title: String) {
    PARTICIPANT(0.toByte(), "Участник"),
    ORGANIZER(1.toByte(), "Организатор"),
    ORIGINATOR(2.toByte(), "Основатель");

    companion object {
        fun getUserRank(event: Event, user: User) = when (user.id) {
            event.originator -> Ranks.ORIGINATOR
            in event.organizers -> Ranks.ORGANIZER
            else -> Ranks.PARTICIPANT
        }
    }
}
