package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserEmailCode(
    val email: String,
    val code: String
)