package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String?,
    val name: String,
    val dateOfBirth: String
)