package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.data.DTO.ChatAdministratorUpdate
import pachmp.meventer.data.DTO.ChatNameUpdate
import pachmp.meventer.data.DTO.ChatParticipantUpdate
import pachmp.meventer.data.DTO.Message
import javax.inject.Inject

class ChatRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    private val repositoryURL = baseURL + "chat/"

    suspend fun createClosedChat() = withHttpClient {
        post("${repositoryURL}create/closed") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
        }.toResponse<Unit>()
    }

    suspend fun createDialog(userID: Int) = withHttpClient {
        post("${repositoryURL}create/dialog") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(userID)
        }.toResponse<Unit>()
    }

    suspend fun getParticipants() = withHttpClient {
        post("${repositoryURL}participants") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
        }.toResponse<List<Int>>()
    }

    suspend fun getAllChats() = withHttpClient {
        post("${repositoryURL}getAll/chats") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
        }.toResponse<List<Chat>>()
    }

    suspend fun getAllMessages(chatID: Long) = withHttpClient {
        post("${repositoryURL}getAll/messages") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(chatID)
        }.toResponse<List<Message>>()
    }

    suspend fun changeParticipant(chatParticipantUpdate: ChatParticipantUpdate) = withHttpClient {
        post("${repositoryURL}change/participant") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(chatParticipantUpdate)
        }.toResponse<Unit>()
    }

    suspend fun changeAdministrator(chatAdministratorUpdate: ChatAdministratorUpdate) = withHttpClient {
        post("${repositoryURL}change/administrator") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(chatAdministratorUpdate)
        }.toResponse<Unit>()
    }

    suspend fun changeChatName(chatNameUpdate: ChatNameUpdate) = withHttpClient {
        post("${repositoryURL}change/name") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(chatNameUpdate)
        }.toResponse<Unit>()
    }

    suspend fun deleteChat(chatID: Long) = withHttpClient {
        post("${repositoryURL}delete") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(chatID)
        }.toResponse<Unit>()
    }
}
