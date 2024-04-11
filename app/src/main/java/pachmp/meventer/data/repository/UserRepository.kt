package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json.Default.encodeToString
import pachmp.meventer.data.DTO.NullableUserID
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserFeedback
import pachmp.meventer.data.DTO.UserFeedbackCreate
import pachmp.meventer.data.DTO.UserFeedbackUpdate
import pachmp.meventer.data.DTO.UserLogin
import pachmp.meventer.data.DTO.UserRegister
import pachmp.meventer.data.DTO.UserUpdate
import pachmp.meventer.data.DTO.UserUpdateEmail
import pachmp.meventer.data.DTO.UserUpdatePassword
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context,
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    private val repositoryURL = baseURL + "user/"
    suspend fun createFeedback(userFeedbackCreate: UserFeedbackCreate) = withHttpClient {
        post("${repositoryURL}feedback/create") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userFeedbackCreate)
        }.toResponse<Unit>()
    }

    suspend fun updateFeedback(userFeedbackUpdate: UserFeedbackUpdate) = withHttpClient {
        post("${repositoryURL}feedback/update") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userFeedbackUpdate)
        }.toResponse<Unit>()
    }

    suspend fun deleteFeedback(FeedbackID: Long) = withHttpClient {
        post("${repositoryURL}feedback/delete") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(FeedbackID)
        }.toResponse<Unit>()
    }

    suspend fun getFeedbacks(userID: Int? = null) = withHttpClient {
        post("${repositoryURL}feedback/get") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userID)
        }.toResponse<List<UserFeedback>>()
    }

    suspend fun getUserData(userID: Int? = null) = withHttpClient {
        post("${repositoryURL}data") {
            contentType(ContentType.Application.Json)
            if (userID!=null) {
                setBody(userID)
            }
            bearerAuth(getToken())
        }.toResponse<User>()
    }


    suspend fun verifyToken() = withHttpClient {
        get("${repositoryURL}verifyToken") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
        }.toResponse<Unit>()
    }

    suspend fun sendEmailCode(email: String) = withHttpClient {
        post("${repositoryURL}sendEmailCode") { setBody(email) }
            .toResponse<Unit>()
    }

    suspend fun verifyEmailCode(userEmailCode: UserEmailCode) = withHttpClient {
        post("${repositoryURL}verifyEmailCode") {
            contentType(ContentType.Application.Json)
            setBody(userEmailCode)
        }.toResponse<Unit>()
    }

    suspend fun register(userRegister: UserRegister, avatar: File? = null) = withHttpClient {
        post("${repositoryURL}register") {
            contentType(ContentType.Application.Json)
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "user",
                            value = encodeToString(UserRegister.serializer(), userRegister),
                            headers = headers {
                                append(
                                    HttpHeaders.ContentType,
                                    "application/json"
                                )
                            })

                        if (avatar != null) {
                            append(
                                key = "avatar",
                                value = avatar.readBytes(),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${avatar.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${avatar.name}\""
                                    )
                                }
                            )
                        }
                    }
                )
            )
        }.toResponse<String>()
    }

    suspend fun login(userLogin: UserLogin) = withHttpClient {
        post("${repositoryURL}login") {
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }.toResponse<String>()
    }

    @OptIn(InternalAPI::class)
    suspend fun updateUserData(userUpdate: UserUpdate, avatar: File?) = withHttpClient {
        post("${repositoryURL}update/data") {
            println(avatar!!.extension)

            bearerAuth(getToken())
            setBody(MultiPartFormDataContent(
                parts = formData {
                    append(
                        key = "user",
                        value = encodeToString(UserUpdate.serializer(), userUpdate),
                        headers = headers {
                            append(
                                HttpHeaders.ContentType,
                                "application/json"
                            )
                        })

                    if (avatar != null) {
                        append(
                            key = "avatar",
                            value = avatar.readBytes(),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "image/${avatar.extension}"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${avatar.name}\""
                                )
                            }
                        )
                    }
                }
            ))
        }.toResponse<Unit>()
    }

    suspend fun updateUserEmail(userUpdateEmail: UserUpdateEmail) = withHttpClient {
        post("${repositoryURL}update/email") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userUpdateEmail)
        }.toResponse<Unit>()
    }

    suspend fun updateUserPassword(userUpdatePassword: UserUpdatePassword) = withHttpClient {
        post("${repositoryURL}update/password") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userUpdatePassword)
        }.toResponse<Unit>()
    }
}