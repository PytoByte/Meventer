package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserFeedbackCreate(
    val toUserID: Int,
    val rating: Float,
    val comment: String
)
