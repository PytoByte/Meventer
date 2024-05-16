package pachmp.meventer.components.mainmenu.components.chats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.ChatScreenDestination
import pachmp.meventer.components.destinations.ChatsScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.data.DTO.UserShort
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories)  {

    var query by mutableStateOf("")
    var chats by mutableStateOf<List<Chat>?>(null)
    var visibleChats by mutableStateOf<List<Chat>?>(null)
    var selectedChat by mutableStateOf<Chat?>(null)
    var selectedUserID by mutableStateOf<Int?>(null)
    var users by mutableStateOf<List<UserShort>?>(null)

    fun initSocket() {
        if (repositories.chatSocketRepository.isSessionInit().not()) {
            repositories.chatSocketRepository.initSocket()
        }
    }

    fun updateChats() {
        viewModelScope.launch {
            afterCheckResponse(repositories.chatRepository.getAllChats()) { response ->
                chats = response.data!!
            }

            repositories.chatSocketRepository.clearListeners()
            repositories.chatSocketRepository.addOnMessageListener { message ->
                chats?.let {
                    val chat = it.find { chat -> chat.chatID==message.chatID }
                    if (chat!=null) {
                        if (chat.lastMessages.isEmpty().not()) {
                            chat.lastMessages = chat.lastMessages-chat.lastMessages.first()+message
                        } else {
                            chat.lastMessages = chat.lastMessages+message
                        }
                    } else {
                        updateChats()
                        return@let
                    }
                }
            }

            visibleChats = chats
        }
    }

    fun navigateToDialog(userID: Int) {
        selectedUserID = userID
        navigator.navigate(ChatScreenDestination())
    }

    fun navigateToChat(chat: Chat) {
        selectedChat = chat
        navigator.navigate(ChatScreenDestination)
    }

    fun navigateToAllChats() {
        navigator.clearNavigate(ChatsScreenDestination)
        selectedUserID = null
    }

    fun filterChats() {
        visibleChats = chats!!.filter {
            it.name.contains(query)
        }
    }

    fun clearSearch() {
        viewModelScope.launch {
            users=null
            updateChats()
        }
    }

    fun findChats(globalSearch: Boolean) {
        viewModelScope.launch {
            if (globalSearch) {
                afterCheckResponse(repositories.userRepository.getUsersByNick(query)) { response ->
                    users = response.data!!
                }
            } else {
                users=null
                updateChats()
                filterChats()
            }
        }
    }
}