package pachmp.meventer.components.mainmenu.components.events

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.AllEventsScreenDestination
import pachmp.meventer.components.destinations.CreateEventScreenDestination
import pachmp.meventer.components.destinations.EventScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventsGet
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories
) : BottomViewModel(rootNavigator, navigator, repositories)  {
    var events by mutableStateOf<List<Event>?>(null)
        private set
    var user by mutableStateOf<User?>(null)
        private set

    private var selected: Event? = null

    fun getSelected(): Event? {
        return selected
    }

    fun setSelected(selected: Event) {
        this.selected = selected
    }

    fun updateEvents() {
        viewModelScope.launch {
            val responseUser = repositories.userRepository.getUserData()
            if (checkResponse(responseUser)) {
                user = responseUser!!.data
            }
            val responseEvents = repositories.eventRepository.getUserEvents(EventsGet(actual = null, aforetime = null, type = null))
            if (checkResponse(responseEvents)) {
                events = responseEvents!!.data
            }
        }
    }

    fun changeLike(event: Event) {
        viewModelScope.launch {
            val response = repositories.eventRepository.changeFavourite(event.id)
            if (checkResultResponse(response)) {
                val responseEvents = repositories.eventRepository.getUserEvents(EventsGet(actual = null, aforetime = null, type = null))
                if (checkResponse(responseEvents)) {
                    events = responseEvents!!.data
                }
            }
        }
    }

    fun navigateToEvent(event: Event) {
        viewModelScope.launch {
            val response = repositories.eventRepository.getEvent(event.id)
            if (checkResponse(response)) {
                setSelected(response!!.data!!)
                if (selected==null) {
                    snackbarHostState.showSnackbar("Мероприятие не найдено")
                } else {
                    navigator.clearNavigate(EventScreenDestination)
                }
            }
        }
    }

    fun navigateToAllEvents() {
        viewModelScope.launch {
            updateEvents()
            navigator.clearNavigate(AllEventsScreenDestination)
        }
    }

    fun navigateToCreateEvent() {
        viewModelScope.launch {
            navigator.clearNavigate(CreateEventScreenDestination)
        }
    }
}