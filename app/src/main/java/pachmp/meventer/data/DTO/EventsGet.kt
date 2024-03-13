package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class EventsGet(
    val userID: Int?=null,
    val actual: Boolean?,
    val aforetime: Boolean?,
    val type: String?
)