package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
import pachmp.meventer.data.DTO.NullableUserID
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserFeedback
import pachmp.meventer.data.DTO.UserFeedbackCreate
import pachmp.meventer.data.DTO.UserLogin
import pachmp.meventer.data.DTO.UserRegister
import pachmp.meventer.data.DTO.UserUpdate
import pachmp.meventer.data.DTO.UserUpdateEmail
import pachmp.meventer.data.DTO.UserUpdatePassword
import java.io.File
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

class UserRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context,
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    val repositoryURL = baseURL + "user/"
    suspend fun createFeedback(userFeedbackCreate: UserFeedbackCreate) = withHttpClient {
        post("${repositoryURL}data") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userFeedbackCreate)
        }.body<ResultResponse>()
    }

    suspend fun getFeedbacks(nullableUserID: NullableUserID = NullableUserID(null)) = withHttpClient {
        post("${repositoryURL}feedback/get") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(nullableUserID)
        }.body<Response<List<UserFeedback>>>()
    }

    suspend fun getUserData(nullableUserID: NullableUserID = NullableUserID(null)) = withHttpClient {
        post("${repositoryURL}data") {
            contentType(ContentType.Application.Json)
            setBody(nullableUserID)
            bearerAuth(getToken())
        }.body<Response<User>>()
    }


    suspend fun verifyToken() = withHttpClient {
        get("${repositoryURL}verifyToken") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
        }.body<ResultResponse>()
    }

    suspend fun sendEmailCode(email: String) = withHttpClient {
        post("${repositoryURL}sendEmailCode") { setBody(email) }
            .body<ResultResponse>()
    }

    suspend fun verifyEmailCode(userEmailCode: UserEmailCode) = withHttpClient {
        post("${repositoryURL}verifyEmailCode") {
            contentType(ContentType.Application.Json)
            setBody(userEmailCode)
        }.body<ResultResponse>()
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
        }.body<Response<String>>()
    }

    suspend fun login(userLogin: UserLogin) = withHttpClient {
        post("${repositoryURL}login") {
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }.body<Response<String>>()
    }

    @OptIn(InternalAPI::class)
    suspend fun updateUserData(userUpdate: UserUpdate, avatar: File?) = withHttpClient {
        post("${repositoryURL}update/data") {
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
        }.body<ResultResponse>()
    }

    suspend fun updateUserEmail(userUpdateEmail: UserUpdateEmail) = withHttpClient {
        post("${repositoryURL}update/email") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userUpdateEmail)
        }.body<ResultResponse>()
    }

    suspend fun updateUserPassword(userUpdatePassword: UserUpdatePassword) = withHttpClient {
        post("${repositoryURL}update/password") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userUpdatePassword)
        }.body<ResultResponse>()
    }
}