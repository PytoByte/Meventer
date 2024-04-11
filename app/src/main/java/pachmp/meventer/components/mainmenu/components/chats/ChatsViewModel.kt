package pachmp.meventer.components.mainmenu.components.chats

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.ChatScreenDestination
import pachmp.meventer.components.destinations.ChatsScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.components.mainmenu.MainMenuViewModel
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories)  {

    var chats by mutableStateOf<List<Chat>?>(null)
    var selectedChat by mutableStateOf<Chat?>(null)

    fun initSocket() {
        if (repositories.chatSocketRepository.isSessionInit().not()) {
            repositories.chatSocketRepository.initSocket()
        }
    }

    fun updateChats() {
        viewModelScope.launch {
            repositories.chatSocketRepository.addOnMessageListener { message ->
                chats?.let {
                    val chat = it.find { chat -> chat.chatID==message.chatID }
                    if (chat!=null) {
                        chat.lastMessages = chat.lastMessages+message-chat.lastMessages.first()
                    }
                }
            }

            afterCheckResponse(repositories.chatRepository.getAllChats()) { response ->
                chats = response.data!!
            }
        }
    }

    fun navigateToChat(chat: Chat) {
        selectedChat = chat
        navigator.navigate(ChatScreenDestination)
    }

    fun navigateToAllChats() {
        navigator.clearNavigate(ChatsScreenDestination)
    }
}