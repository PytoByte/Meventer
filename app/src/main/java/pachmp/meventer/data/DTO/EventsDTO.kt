@file:UseSerializers(InstantSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.R
import pachmp.meventer.data.Serializers.InstantSerializer
import java.time.Instant

@Serializable
data class Event(
    @SerialName("eventID")
    val id: Int,
    val name: String,
    val images: List<String>,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short,
    val maximalAge: Short?,
    val price: Int,
    val originator: Int,
    val organizers: List<Int>,
    val participants: List<Int>,
    val inFavourites: List<Int>,
    val tags: List<String>
)

@Serializable
data class EventCreate(
    val name: String,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?,
    val tags: List<String>?
)

@Serializable
class EventParticipant(
    val changingID: Int? = null,
    val eventID: Int
)

@Serializable
data class EventSelection(
    val tags: List<String>,
    val age: Short?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String?
) {
    enum class SortingState(val state: String, val nameResourceID: Int) {
        NEAREST_ONES_FIRST("Nearest ones first", R.string.sort_start_soon),
        FURTHER_ONES_FIRST("Further ones first", R.string.sort_start_far)
    }
}

@Serializable
data class EventsGet(
    val userID: Int?=null,
    val actual: Boolean?,
    val aforetime: Boolean?,
    val type: String?
)

@Serializable
data class EventUpdate(
    val eventID: Int,
    val name: String?,
    val description: String?,
    val startTime: Instant?,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?,
    val tags: List<String>?,
    val deletedImages: List<String>?
)

@Serializable
class EventOrganizer(
    val eventID: Int,
    val changingID: Int
)
