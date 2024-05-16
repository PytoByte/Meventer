package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

    fun addOnMessageListener(onMessage: (Message) -> Unit) {
        onMessageListeners.add(onMessage)
        Log.w("SOCKET", "Добавлен слушатель на новые сообщения\n" +
                "Количество: ${onMessageListeners.size}")
    }

    fun addOnMessageUpdateListener(onMessageUpdate: (MessageUpdated) -> Unit) {
        onMessageUpdateListeners.add(onMessageUpdate)
        Log.w("SOCKET", "Добавлен слушатель на обновления сообщений\n" +
                "Количество: ${onMessageUpdateListeners.size}")
    }

    fun addOnMessageDeleteListener(onMessageDelete: (messageID: Long) -> Unit) {
        onMessageDeleteListeners.add(onMessageDelete)
        Log.w("SOCKET", "Добавлен слушатель на удаление сообщений\n" +
                "Количество: ${onMessageDeleteListeners.size}")
    }

    fun clearListeners() {
        Log.w("SOCKET", "Слушатели очищены")
        onMessageListeners.clear()
        onMessageUpdateListeners.clear()
        onMessageDeleteListeners.clear()
    }

    fun isSessionInit(): Boolean {
        return session!=null
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun initSocket() {
        GlobalScope.launch(Dispatchers.Default) {
            session = Mutex().withLock {
                withHttpClient {
                    webSocketSession(method = HttpMethod.Get) {
                        url("wss://${serverIP}:8080/chat/socket")
                        bearerAuth(getToken())
                    }
                }
            }
            if (session==null) {
                Log.e("SOCKET", "Не удалось инииализировать сокет при соединении")
            } else if (session!!.isActive) {
                Log.d("SOCKET", "Сессия готова")
                initReceiver()
            } else {
                session = null
                Log.e("SOCKET", "Не удалось инииализировать сокет, моментальный разрыв")
            }
        }
    }

    suspend fun closeSocket() {
        session!!.close(CloseReason(CloseReason.Codes.GOING_AWAY, "End by myself"))
        Log.d("SOCKET", "Сессия закрыта")
    }

    suspend fun send(messageSend: MessageSend)  {
        session!!.send(Json.encodeToString(MessageSend.serializer(), messageSend))
        Log.d("SOCKET", "Сообщение отправлено")
    }

    suspend fun update(messageUpdate: MessageUpdate)  {
        session!!.send(Json.encodeToString(MessageUpdate.serializer(), messageUpdate))
        Log.d("SOCKET", "Сообщение обновлено")
    }

    suspend fun delete(messageDelete: MessageDelete) {
        session!!.send(Json.encodeToString(MessageDelete.serializer(), messageDelete))
        Log.d("SOCKET", "Сообщение удалено")
    }

    private suspend fun initReceiver() {
        Log.d("SOCKET", "Прослушивание..")
        try {
            session!!.incoming.consumeEach { frame ->
                val receive = frame.data.decodeToString()
                Log.d("SOCKET", "Получено $receive")
                try {
                    val decodedReceive = Json.decodeFromString<Message>(receive)
                    onMessageListeners.forEachIndexed { index, listener ->
                        try {
                            listener(decodedReceive)
                        } catch (e: Exception) {
                            onMessageListeners.removeAt(index)
                            e.printStackTrace()
                            Log.w("SOCKET", "^^^ удалён слушатель новых сообщений по причине выше")
                        }
                    }
                    return@consumeEach
                } catch (_: Exception) { }
                try {
                    val decodedReceive = Json.decodeFromString<MessageUpdated>(receive)
                    onMessageUpdateListeners.forEachIndexed { index, listener ->
                        try {
                            listener(decodedReceive)
                        } catch (e: Exception) {
                            onMessageUpdateListeners.removeAt(index)
                            e.printStackTrace()
                            Log.w("SOCKET", "^^^ удалён слушатель обновления сообщений по причине выше")
                        }
                    }
                    return@consumeEach
                } catch (_: Exception) { }
                try {
                    val decodedReceive = receive.toLong()
                    onMessageDeleteListeners.forEachIndexed { index, listener ->
                        try {
                            listener(decodedReceive)
                        } catch (e: Exception) {
                            onMessageDeleteListeners.removeAt(index)
                            e.printStackTrace()
                            Log.w("SOCKET", "^^^ удалён слушатель удаления сообщений по причине выше")
                        }
                    }
                    return@consumeEach
                } catch (_: Exception) {}
                throw UnknownError("(SOCKET) Не удалось декодировать сообщение с сервера")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SOCKET", "^^^ Соединение разорвано")
        }
    }
}