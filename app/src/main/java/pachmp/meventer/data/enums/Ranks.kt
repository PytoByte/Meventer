package pachmp.meventer.data.enums

import pachmp.meventer.R
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.User

enum class Ranks(val value: Byte, val titleResourceID: Int) {
    PARTICIPANT(0.toByte(), R.string.participant),
    ORGANIZER(1.toByte(), R.string.organizer),
    ORIGINATOR(2.toByte(), R.string.originator);

    companion object {
        fun getUserRank(event: Event, user: User) = when (user.id) {
            event.originator -> Ranks.ORIGINATOR
            in event.organizers -> Ranks.ORGANIZER
            else -> Ranks.PARTICIPANT
        }
    }
}
