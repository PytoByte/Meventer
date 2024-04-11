package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import pachmp.meventer.data.DTO.Message
import pachmp.meventer.data.DTO.MessageDelete
import pachmp.meventer.data.DTO.MessageSend
import pachmp.meventer.data.DTO.MessageUpdate
import pachmp.meventer.data.DTO.MessageUpdated
import javax.inject.Inject

class ChatSocketRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context
) : DefaultRepository(encryptedSharedPreferences, appContext) {

    private var session: DefaultClientWebSocketSession? = null

    private val onMessageListeners = mutableListOf<(Message) -> Unit>()
    private val onMessageUpdateListeners = mutableListOf<(MessageUpdated) -> Unit>()
    private val onMessageDeleteListeners = mutableListOf<(msgID: Long) -> Unit>()

    fun addOnMessageListener(onMessage: (Message) -> Unit) =
        onMessageListeners.add(onMessage)

    fun addOnMessageUpdateListener(onMessageUpdate: (MessageUpdated) -> Unit) =
        onMessageUpdateListeners.add(onMessageUpdate)

    fun addOnMessageDeleteListener(onMessageDelete: (messageID: Long) -> Unit) =
        onMessageDeleteListeners.add(onMessageDelete)

    fun isSessionInit(): Boolean {
        return session!=null
    }

    fun initSocket() {
        GlobalScope.launch(Dispatchers.Default) {
            session = Mutex().withLock {
                withHttpClient {
                    webSocketSession(method = HttpMethod.Get) {
                        url("wss://89.23.99.58:80/chat/socket")
                        bearerAuth(getToken())
                    }
                } ?: throw InstantiationException("(SOCKET) Не удалось инииализировать сокет")
            }
            if (session!!.isActive) {
                Log.d("SOCKET", "Сессия готова")
                initReceiver()
            } else throw InstantiationException("(SOCKET) Не удалось инииализировать сокет")
        }
    }

    suspend fun closeSocket() {
        session!!.close(CloseReason(CloseReason.Codes.GOING_AWAY, "End by myself"))
    }

    suspend fun send(messageSend: MessageSend)  {
        session!!.send(Json.encodeToString(MessageSend.serializer(), messageSend))
    }

    suspend fun send(messageUpdate: MessageUpdate)  {
        session!!.send(Json.encodeToString(MessageUpdate.serializer(), messageUpdate))
    }

    suspend fun send(messageDelete: MessageDelete) {
        session!!.send(Json.encodeToString(MessageDelete.serializer(), messageDelete))
    }

    private suspend fun initReceiver() {
        Log.d("SOCKET", "Прослушивание..")
        session!!.incoming.consumeEach { frame ->
            val receive = frame.data.decodeToString()
            Log.d("SOCKET", "Получено $receive")
            try {
                val decodedReceive = Json.decodeFromString<Message>(receive)
                onMessageListeners.forEach {
                    it(decodedReceive)
                }
                return@consumeEach
            } catch (_: Exception) { }
            try {
                val decodedReceive = Json.decodeFromString<MessageUpdated>(receive)
                onMessageUpdateListeners.forEach {
                    it(decodedReceive)
                }
                return@consumeEach
            } catch (_: Exception) { }
            try {
                val decodedReceive = receive.toLong()
                onMessageDeleteListeners.forEach {
                    it(decodedReceive)
                }
                return@consumeEach
            } catch (_: Exception) {}
            throw UnknownError("(SOCKET) Не удалось декодировать сообщение с сервера")
        }
    }
}