package pachmp.meventer.components.mainmenu.components.events

import android.net.Uri
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
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.DTO.EventSelection
import pachmp.meventer.data.DTO.EventsGet
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var events by mutableStateOf<List<Event>?>(null)
        private set

    var eventsVisible by mutableStateOf<List<Event>?>(null)
        private set

    var user by mutableStateOf<User?>(null)
        private set

    var selected: Event? = null

    var query by mutableStateOf("")

    var eventSelection by mutableStateOf(EventSelection(emptyList(), 0, 0, null, EventSelection.SortingStates.NEAREST_ONES_FIRST.state))

    var favoriteFilter by mutableStateOf(false)
    var participantFilter by mutableStateOf(false)
    var organizerFilter by mutableStateOf(false)
    var originatorFilter by mutableStateOf(false)

    fun filterByFastTags() {
        if (events!=null) {
            eventsVisible = events!!.filter {
                if (!favoriteFilter && !participantFilter && !organizerFilter && !originatorFilter) {
                    true
                } else if (participantFilter && (user!!.id in it.participants || user!!.id in it.organizers || user!!.id ==it.originator)) {
                    true
                } else if (organizerFilter && (user!!.id in it.organizers)) {
                    true
                } else if (favoriteFilter && (user!!.id in it.inFavourites)) {
                    true
                } else originatorFilter && (user!!.id == it.originator)
            }
        }
    }

    fun updateEvents() {
        viewModelScope.launch {
            val responseUser = repositories.userRepository.getUserData()
            if (checkResponse(responseUser)) {
                user = responseUser!!.data
            }
            val responseEvents = repositories.eventRepository.getUserEvents(
                EventsGet(
                    actual = null,
                    aforetime = null,
                    type = null
                )
            )
            if (checkResponse(responseEvents) {
                    if (it.code==204.toShort()) { events = emptyList(); false}
                    else null
            }) {
                events = List(responseEvents!!.data!!.size) {
                    fixEventImages(responseEvents.data!![it])
                }
            }
            eventsVisible = events
        }
    }

    fun changeLike(event: Event) {
        viewModelScope.launch {
            val request = repositories.eventRepository.changeFavourite(event.id)
            if (checkResultResponse(request)) {
                updateEvents()
            }
        }
    }

    fun navigateToEvent(event: Event) {
        viewModelScope.launch {
            val response = repositories.eventRepository.getEvent(event.id)
            if (checkResponse(response)) {
                selected = fixEventImages(response!!.data!!)
                if (selected == null) {
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

    fun createEvent(eventCreate: EventCreate?, images: List<Uri>) {
        viewModelScope.launch {
            if (eventCreate != null) {
                val files = List(images.size) {
                    cacheFile(images[it], "image${it}")
                }
                val response = repositories.eventRepository.createEvent(eventCreate, files)
                if (checkResultResponse(response)) {
                    navigateToAllEvents()
                    snackbarHostState.showSnackbar("Мероприятие успешно создано")
                }
            } else {
                snackbarHostState.showSnackbar("Некоторые поля не заполненны или заполненны неверно. Проверьте актуальность даты создания")
            }
        }
    }

    fun searchEvents() {
        viewModelScope.launch {
            eventSelection = eventSelection.copy(tags = (eventSelection.tags ?: emptyList())+listOf(query))
            val response = repositories.eventRepository.getGlobalEvents(eventSelection = eventSelection)
            if (checkResponse(response) {false}) {
                snackbarHostState.showSnackbar("Найдено ${response!!.data!!.size} мероприятий")
                events = response.data!!
            } else {
                snackbarHostState.showSnackbar("Мероприятия не найдены")
            }
        }
    }
}