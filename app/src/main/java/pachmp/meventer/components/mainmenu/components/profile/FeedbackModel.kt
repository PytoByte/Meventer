package pachmp.meventer.components.mainmenu.components.profile

import pachmp.meventer.data.DTO.User

data class FeedbackModel(
    val id: Long,
    val author: User?,
    val rating: Float,
    val comment: String
)