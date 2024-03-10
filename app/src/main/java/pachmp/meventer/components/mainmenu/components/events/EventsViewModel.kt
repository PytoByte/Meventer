package pachmp.meventer.components.mainmenu.components.events

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.EventScreenDestination
import pachmp.meventer.components.destinations.MainMenuScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.User
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    encryptedSharedPreferences: SharedPreferences,
) : BottomViewModel(rootNavigator, navigator, encryptedSharedPreferences) {
    var events by mutableStateOf<Array<Event>?>(null)
        private set
    var user by mutableStateOf<User?>(null)
        private set

    var selected by mutableStateOf<Event?>(null)
        private set


    init {
        viewModelScope.launch {
            user = repository.getUserByToken(encryptedSharedPreferences.getString("token", "")!!)
            events = repository.getUserEvents().toTypedArray()
        }
    }

    fun changeLike(event: Event) {
        viewModelScope.launch {
            repository.changeFavourite(event.id)
        }
    }

    fun navigateToEvent(event: Event) {
        viewModelScope.launch {
            selected = repository.getEventByID(event.id)
            rootNavigator.clearNavigate(EventScreenDestination)
        }
    }

    fun navigateToAllEvents() {
        viewModelScope.launch {
            events = repository.getUserEvents().toTypedArray()
            rootNavigator.clearNavigate(MainMenuScreenDestination)
        }
    }
}