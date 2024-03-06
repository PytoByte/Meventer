package pachmp.meventer.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response<Type>(
    val result: ResultResponse,
    val data: Type? = null
)

@Serializable
data class ResultResponse(
    @SerialName("code")
    val statusCode: Short,
    val message: String
)

@Serializable
data class UserEmailCode(
    val email: String,
    val code: String
)

@Serializable
data class UserLogin(
    val email: String,
    val password: String
)

@Serializable
data class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String? = null,
    val dateOfBirth: String
)