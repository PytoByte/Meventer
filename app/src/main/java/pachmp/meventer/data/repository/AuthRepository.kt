package pachmp.meventer.data.repository

import android.content.SharedPreferences
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserLogin
import pachmp.meventer.data.DTO.UserRegister
import javax.inject.Inject
import javax.inject.Singleton

class AuthRepository @Inject constructor(encryptedSharedPreferences: SharedPreferences): DefaultRepository(encryptedSharedPreferences) {
    val repositoryURL = baseURL+"event/"

    suspend fun sendEmailCode(email: String) = withHttpClient {
        post("${repositoryURL}user/sendEmailCode") { setBody(email) }
            .body<ResultResponse>()
    }

    suspend fun verifyEmailCode(userEmailCode: UserEmailCode) = withHttpClient {
        post("${repositoryURL}user/verifyEmailCode") {
            contentType(ContentType.Application.Json)
            setBody(userEmailCode)
        }.body<ResultResponse>()
    }

    suspend fun register(userRegister: UserRegister) = withHttpClient {
        post("${repositoryURL}user/register") {
            contentType(ContentType.Application.Json)
            setBody(userRegister)
        }.body<Response<String>>()
    }

    suspend fun login(userLogin: UserLogin) = withHttpClient {
        post("${repositoryURL}user/login") {
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }.body<Response<String>>()
    }
}