package pachmp.meventer.components.mainmenu.components.chats.components

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.R
import pachmp.meventer.RootNav
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.data.DTO.Message
import pachmp.meventer.data.DTO.MessageDelete
import pachmp.meventer.data.DTO.MessageSend
import pachmp.meventer.data.DTO.MessageUpdate
import pachmp.meventer.data.DTO.MessageUpdated
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.enums.FileType
import pachmp.meventer.data.repository.Repositories
import java.io.File
import java.lang.Integer.max
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var textToSend by mutableStateOf("")
    private val messages by mutableStateOf(mutableStateListOf<Message>())
    val messagesVisible by mutableStateOf(mutableStateListOf<Message>())
    val messagesTemp by mutableStateOf(mutableStateListOf<MessageSend>())
    var appUser by mutableStateOf<User?>(null)
    var chat by mutableStateOf<Chat?>(null)
    var picture by mutableStateOf<String?>(null)
    var selectedMessage by mutableStateOf<Message?>(null)
        private set

    var selectedFilesUris by mutableStateOf(emptyList<Pair<FileType, Uri>>())
        private set

    fun extendSelectedFileUris(uris: List<Uri>) {
        val pairs = uris.map {
            Pair(FileType.getFileType(getFileExtension(it)), it)
        }
        selectedFilesUris += pairs
    }

    fun removeFile(uri: Uri) {
        selectedFilesUris = selectedFilesUris.filter { it.second != uri }
    }

    private fun recieveMessage(message: Message) {
        if (message.chatID == chat!!.chatID) {
            messagesVisible.add(message)
        }
    }

    private fun updateMessage(messageUpdated: MessageUpdated) {
        val messageIndex = messages.indexOfFirst {
            it.id == messageUpdated.id
        }
        val visibleMessageIndex = messagesVisible.indexOfFirst {
            it.id == messageUpdated.id
        }

        if (messageIndex != -1) {
            messages[messageIndex] = messages[messageIndex].copy(body = messageUpdated.body)
        } else if (visibleMessageIndex != -1) {
            messagesVisible[visibleMessageIndex] = messagesVisible[visibleMessageIndex].copy(body = messageUpdated.body)
        }
    }

    private fun deleteMessage(id: Long) {
        val messageIndex = messages.indexOfFirst {
            it.id == id
        }
        val visibleMessageIndex = messagesVisible.indexOfFirst {
            it.id == id
        }

        if (messageIndex != -1) {
            messages.removeAt(messageIndex)
        } else if (visibleMessageIndex != -1) {
            messagesVisible.removeAt(visibleMessageIndex)
        }
    }

    fun initFromChat(chat: Chat) {
        this.chat = chat

        viewModelScope.launch {
            repositories.chatSocketRepository.addOnMessageListener { recieveMessage(it) }
            repositories.chatSocketRepository.addOnMessageUpdateListener { updateMessage(it) }
            repositories.chatSocketRepository.addOnMessageDeleteListener { deleteMessage(it) }

            afterCheckResponse(repositories.userRepository.getUserData()) { response ->
                appUser = response.data!!
            }

            messagesVisible.addAll(chat.lastMessages.reversed())

            afterCheckResponse(repositories.chatRepository.getAllMessages(chat.chatID)) { response ->
                if (response.data!!.size>chat.lastMessages.size) {
                    messages.addAll(response.data.subList(0, response.data.size-chat.lastMessages.size))
                }
            }
        }
    }

    suspend fun findChatWithUser(userID: Int): Chat? {
        var chat: Chat? = null
        afterCheckResponse(repositories.chatRepository.getAllChats()) { response ->
            val appUserChats = response.data!!
            appUserChats.forEach {
                if (userID in it.participants && it.administrators==null && it.originator==null) {
                    chat = it
                    return@forEach
                }
            }
        }

        return chat
    }

    fun initFromUserID(userID: Int) {
        viewModelScope.launch {
            chat = findChatWithUser(userID)
            if (chat!=null) {
                initFromChat(chat!!)
            } else {
                afterCheckResponse(repositories.chatRepository.createDialog(userID)) {
                    chat = findChatWithUser(userID)
                    initFromChat(chat!!)
                }
            }
        }
    }

    suspend fun initPicture() {
        if (chat!!.originator == null) {
            val userID = chat!!.participants.find { it != appUser!!.id }
            afterCheckResponse(repositories.userRepository.getUserData(userID)) { response ->
                picture = response.data!!.avatar
            }
        } else {
            picture = "https://cdn-icons-png.flaticon.com/512/134/134914.png"
        }
    }

    fun addMessages() {
        if (messages.isEmpty().not()) {
            Log.i(
                "Chat",
                "ADDING MESSAGES\nmessagesVisible size: ${messagesVisible.size}\nmessages size: ${messages.size}\nadd range: ${
                    max(
                        0,
                        messages.size - 10
                    )
                }..${messages.size}"
            )
            messagesVisible.addAll(0, messages.subList(max(0, messages.size - 10), messages.size))
            messages.removeRange(max(0, messages.size - 10), messages.size)
        }
    }

    fun selectMessage(message: Message) {
        selectedMessage = message
        textToSend = message.body
        selectedFilesUris = emptyList()
    }

    fun unselectMessage() {
        selectedMessage = null
        textToSend = ""
    }

    fun send() {
        viewModelScope.launch {
            val text = textToSend
            var attachment: File? = null
            var serverAttachment: String? = null
            val filesUris = selectedFilesUris

            textToSend = ""
            selectedFilesUris = emptyList()

            if (filesUris.isNotEmpty()) {
                attachment = cacheFile(filesUris[0].second, "attachment")
            }

            val uploadMessage = MessageSend(
                chat!!.chatID,
                text+"\n"+ repositories.appContext.getString(R.string.file_uploading),
                Instant.now(),
                null
            )
            messagesTemp.add(uploadMessage)
            if (attachment!=null) {
                afterCheckResponse(repositories.fileRepository.upload(attachment)) {
                    serverAttachment = it.data
                }
            }
            messagesTemp.remove(uploadMessage)

            val messageSend = MessageSend(
                chat!!.chatID,
                text,
                Instant.now(),
                serverAttachment
            )

            launch {
                messagesTemp.add(messageSend)
                repositories.chatSocketRepository.send(messageSend)
                messagesTemp.remove(messageSend)
            }

            if (filesUris.size>1) {
                sendFiles(filesUris.subList(1, filesUris.size).map{it.second})
            }
        }
    }

    suspend fun sendFiles(filesUri: List<Uri>) {
        filesUri.forEach { uri ->
            val attachment = cacheFile(uri, "attachment")
            var serverAttachment: String? = null

            val uploadMessage = MessageSend(
                chat!!.chatID,
                repositories.appContext.getString(R.string.file_uploading),
                Instant.now(),
                null
            )

            messagesTemp.add(uploadMessage)
            if (attachment!=null) {
                afterCheckResponse(repositories.fileRepository.upload(attachment)) {
                    serverAttachment = it.data
                }
            }
            messagesTemp.remove(uploadMessage)

            val messageSend = MessageSend(
                chat!!.chatID,
                textToSend,
                Instant.now(),
                serverAttachment
            )

            viewModelScope.launch {
                messagesTemp.add(messageSend)
                repositories.chatSocketRepository.send(messageSend)
                messagesTemp.remove(messageSend)
            }
        }
    }

    fun update() {
        viewModelScope.launch {
            repositories.chatSocketRepository.update(
                MessageUpdate(
                    selectedMessage!!.id,
                    selectedMessage!!.chatID,
                    textToSend
                )
            )
            selectedMessage = null
            textToSend = ""
        }
    }

    fun delete(message: Message) {
        viewModelScope.launch {
            repositories.chatSocketRepository.delete(
                MessageDelete(
                    message.id,
                    message.chatID
                )
            )
            textToSend = ""
        }
    }


}