@file:UseSerializers(InstantSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.data.Serializers.InstantSerializer
import java.time.Instant

@Serializable
data class Event(
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
    val inFavourites: List<Int>
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
data class EventSelection(
    val tags: List<String>?,
    val age: Short?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String?
) {
    enum class SortingStates(val state: String) {
        NEAREST_ONES_FIRST("Nearest ones first"),
        FURTHER_ONES_FIRST("Further ones first")
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
    val price: Int?
)

@Serializable
class EventOrganizer(
    val eventID: Int,
    val changingID: Int
)
