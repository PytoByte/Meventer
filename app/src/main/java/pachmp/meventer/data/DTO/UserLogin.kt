package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class UserLogin(
    val email: String,
    val password: String
)