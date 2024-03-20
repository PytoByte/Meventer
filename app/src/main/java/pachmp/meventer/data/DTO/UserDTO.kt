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
    val id: Long,
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

@Serializable
data class UserUpdate(
    val nickname: String?,
    val name: String?,
)

@Serializable
data class UserUpdateEmail(
    val emailCode: String,
    val email: String,
)

@Serializable
data class UserUpdatePassword(
    val emailCode: String,
    val newPassword: String
)

@Serializable
data class UserShort(
    val id: Int,
    val nickname: String,
    val avatar: String
)

@Serializable
data class UserFeedbackUpdate(
    val FeedbackID: Long,
    val rating: Float,
    val comment: String
)