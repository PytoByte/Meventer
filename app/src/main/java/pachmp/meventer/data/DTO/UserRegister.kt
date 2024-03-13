package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String? = null,
    val dateOfBirth: String
)