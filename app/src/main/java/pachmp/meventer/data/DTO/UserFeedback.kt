package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserFeedback(
    val fromUserID: Int,
    val rating: Float,
    val comment: String
)
