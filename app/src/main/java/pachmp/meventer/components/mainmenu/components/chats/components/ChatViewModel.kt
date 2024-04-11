package pachmp.meventer.components.mainmenu.components.chats.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var textToSend by mutableStateOf("")
    var messages by mutableStateOf(mutableStateListOf<MessageModel>())
    var appUser by mutableStateOf<User?>(null)
    var chat by mutableStateOf<Chat?>(null)
    var cacheUsers by mutableStateOf(mutableStateListOf<User>())

    fun recieveMessage(message: Message) {
        viewModelScope.launch {
            if (message.chatID==chat!!.chatID) {
                val cacheUser = cacheUsers.find { it.id==message.senderID }
                if (cacheUser!=null) {
                    messages.add(
                        MessageModel(message.id,
                            message.chatID,
                            message.body,
                            message.timestamp,
                            cacheUser,
                            message.attachment?.let { listOf(it) }
                        )
                    )
                } else {
                    val senderResponse = repositories.userRepository.getUserData(message.senderID)
                    if (afterCheckResponse(senderResponse)) {
                        messages.add(
                            MessageModel(message.id,
                                message.chatID,
                                message.body,
                                message.timestamp,
                                senderResponse!!.data!!,
                                message.attachment?.let { listOf(it) }
                            )
                        )
                    } else {
                        messages.add(
                            MessageModel(message.id,
                                message.chatID,
                                message.body,
                                message.timestamp,
                                null,
                                message.attachment?.let { listOf(it) }
                            )
                        )
                    }
                }
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

            cacheUsers.add(appUser!!)

            afterCheckResponse(repositories.chatRepository.getAllMessages(chat.chatID)) { response ->
                messages.apply {
                    response.data!!.forEach { message ->
                        val cacheUser = cacheUsers.find { it.id==message.senderID }
                        if (cacheUser!=null) {
                            add(
                                MessageModel(message.id,
                                    message.chatID,
                                    message.body,
                                    message.timestamp,
                                    cacheUser,
                                    message.attachment?.let { listOf(it) }
                                )
                            )
                        } else {
                            val senderResponse = repositories.userRepository.getUserData(message.senderID)
                            if (afterCheckResponse(senderResponse)) {
                                add(
                                    MessageModel(message.id,
                                        message.chatID,
                                        message.body,
                                        message.timestamp,
                                        senderResponse!!.data!!,
                                        message.attachment?.let { listOf(it) }
                                    )
                                )
                            } else {
                                add(
                                    MessageModel(message.id,
                                        message.chatID,
                                        message.body,
                                        message.timestamp,
                                        null,
                                        message.attachment?.let { listOf(it) }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun send() {
        viewModelScope.launch {
            repositories.chatSocketRepository.send(MessageSend(chat!!.chatID, textToSend, Instant.now(), null))
            textToSend = ""
        }
    }
}