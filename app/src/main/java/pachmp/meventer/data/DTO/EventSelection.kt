package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class EventSelection(
    val tags: List<String>?,
    val age: Short?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String?
)
