@file:UseSerializers(LocalDateSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.data.Serializers.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val nickname: String,
    val avatar: String,
    val dateOfBirth: LocalDate
)

@Serializable
data class UserEmailCode(
    val email: String,
    val code: String
)

@Serializable
data class UserFeedback(
    val fromUserID: Int,
    val rating: Float,
    val comment: String
)

@Serializable
data class UserFeedbackCreate(
    val toUserID: Int,
    val rating: Float,
    val comment: String
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
    val nickname: String?,
    val name: String,
    val dateOfBirth: String
)

@Serializable
data class NullableUserID(
    val id: Int? = null
)