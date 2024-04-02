package pachmp.meventer.components.mainmenu

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.data.DTO.MessageSend
import pachmp.meventer.data.repository.Repositories
import java.time.Instant
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories
) : BottomViewModel(rootNavigator, navigator, repositories) {

    init {
        viewModelScope.launch {
            repositories.chatSocketRepository.send(MessageSend(1L, "some text", Instant.now(), null))
        }
    }
}
