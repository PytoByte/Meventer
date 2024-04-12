package pachmp.meventer.components.mainmenu.components.chats.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.components.mainmenu.components.chats.components.screens.MessageModel
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.data.DTO.Message
import pachmp.meventer.data.DTO.MessageSend
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import java.lang.Integer.max
import java.time.Instant
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class ChatViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var textToSend by mutableStateOf("")
    val messages by mutableStateOf(mutableStateListOf<Message>())
    val messagesVisible by mutableStateOf(mutableStateListOf<Message>())
    var appUser by mutableStateOf<User?>(null)
    var chat by mutableStateOf<Chat?>(null)
    var picture by mutableStateOf<String?>(null)

    fun recieveMessage(message: Message) {
        viewModelScope.launch {
            if (message.chatID == chat!!.chatID) {
                messages.add(message)
            }

        }
    }

    fun initFromChat(chat: Chat) {
        this.chat = chat

        viewModelScope.launch {
            repositories.chatSocketRepository.addOnMessageListener { recieveMessage(it) }

            afterCheckResponse(repositories.userRepository.getUserData()) { response ->
                appUser = response.data!!
            }

            messagesVisible.addAll(chat.lastMessages.reversed())

            afterCheckResponse(repositories.chatRepository.getAllMessages(chat.chatID)) { response ->
                messages.clear()
                messages.addAll(response.data!!)
            }
        }
    }

    suspend fun initPicture() {
        if (chat!!.originator==null) {
            val userID = chat!!.participants.find { it!=appUser!!.id }
            afterCheckResponse(repositories.userRepository.getUserData(userID)) { response ->
                picture = response.data!!.avatar
            }
        } else {
            picture = "https://cdn-icons-png.flaticon.com/512/134/134914.png"
        }
    }

    fun addMessages() {
        println("LOOOOAAAADDD ${messagesVisible.size} ${min(messagesVisible.size+10, messages.size)} ${messages.size}")
        if (messages.isEmpty().not()) {
            messagesVisible.addAll(messages.subList(messagesVisible.size, min(messagesVisible.size+10, messages.size)))
        }
    }

    fun send() {
        viewModelScope.launch {
            repositories.chatSocketRepository.send(
                MessageSend(
                    chat!!.chatID,
                    textToSend,
                    Instant.now(),
                    null
                )
            )
            textToSend = ""
        }
    }
}