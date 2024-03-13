package pachmp.meventer.data.repository

import android.content.SharedPreferences
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pachmp.meventer.data.DTO.NullableUserID
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserFeedback
import pachmp.meventer.data.DTO.UserFeedbackCreate
import javax.inject.Inject
import javax.inject.Singleton

class UserRepository @Inject constructor(encryptedSharedPreferences: SharedPreferences): DefaultRepository(encryptedSharedPreferences){
    val repositoryURL = baseURL+"user/"
    suspend fun createFeedback(userFeedbackCreate: UserFeedbackCreate) = withHttpClient {
        post("${repositoryURL}data") {
            setTokenHeader()
            contentType(ContentType.Application.Json)
            setBody(userFeedbackCreate)
        }.body<ResultResponse>()
    }

    suspend fun getFeedbacks(nullableUserID: NullableUserID?=null) = withHttpClient {
        post("${repositoryURL}feedback/get") {
            setTokenHeader()
            contentType(ContentType.Application.Json)
            setBody(nullableUserID)
        }.body<Response<List<UserFeedback>>>()
    }

    suspend fun getUserData(nullableUserID: NullableUserID?=null) = withHttpClient {
        post("${repositoryURL}data") {
            setTokenHeader()
            contentType(ContentType.Application.Json)
            setBody(nullableUserID)
        }.body<Response<User>>()
    }
}