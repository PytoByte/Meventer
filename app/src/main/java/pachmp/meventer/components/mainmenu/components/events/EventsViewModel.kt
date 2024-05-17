package pachmp.meventer.components.mainmenu.components.events

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.R
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.AllEventsScreenDestination
import pachmp.meventer.components.destinations.CreateEventScreenDestination
import pachmp.meventer.components.destinations.EditEventScreenDestination
import pachmp.meventer.components.destinations.EventScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.DTO.EventSelection
import pachmp.meventer.data.DTO.EventUpdate
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

    var eventSelection by mutableStateOf(EventSelection(emptyList(), 0, 0, null, EventSelection.SortingState.NEAREST_ONES_FIRST.state))

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

    fun clearFastTags() {
        favoriteFilter = false
        participantFilter = false
        organizerFilter = false
        originatorFilter = false
    }


    fun updateEvents() = viewModelScope.launch {
            afterCheckResponse(repositories.userRepository.getUserData()) { response ->
                user = response.data!!
            }

            val responseEvents = repositories.eventRepository.getUserEvents(
                EventsGet(
                    actual = null,
                    aforetime = null,
                    type = null
                )
            )

            afterCheckResponse(
                response = responseEvents,
                responseHandler = {
                    if (it.value==204) { events = emptyList(); false} else null
                }
            ) { response -> events = response.data!! }
            eventsVisible = events
        }

    fun changeLike(event: Event) {
        viewModelScope.launch {
            afterCheckResponse(repositories.eventRepository.changeFavourite(event.id)) {
                updateEvents()
            }
        }
    }

    fun navigateToEvent(event: Event) {
        viewModelScope.launch {
            selected = event
            navigator.navigate(EventScreenDestination)
            /*} else {
                snackBarHostState.showSnackbar("Мероприятие не найдено")
            }*/
        }
    }

    fun navigateToDialog() {

    }

    fun navigateToAllEvents() {
        viewModelScope.launch {
            updateEvents()
            navigator.clearNavigate(AllEventsScreenDestination)
        }
    }

    fun navigateToCreateEvent() {
        viewModelScope.launch {
            navigator.navigate(CreateEventScreenDestination)
        }
    }

    fun navigateToEditEvent() {
        viewModelScope.launch {
            navigator.navigate(EditEventScreenDestination)
        }
    }

    fun createEvent(eventCreate: EventCreate?, images: List<Uri>) {
        viewModelScope.launch {
            if (eventCreate != null) {
                val files = List(images.size) {
                    cacheFile(images[it], "image${it}")
                }
                afterCheckResponse(repositories.eventRepository.createEvent(eventCreate, files)) {
                    navigateToAllEvents()
                    snackbarHostState.showSnackbar(repositories.appContext.getString(R.string.event_update_success))
                }
            } else {
                snackbarHostState.showSnackbar(repositories.appContext.getString(R.string.event_validate_error))
            }
        }
    }

    fun editEvent(eventUpdate: EventUpdate?, images: List<Uri>) {
        viewModelScope.launch {
            if (eventUpdate != null) {
                val files = List(images.size) {
                    cacheFile(images[it], "image${it}")
                }
                afterCheckResponse(repositories.eventRepository.editEvent(eventUpdate, files)) {
                    navigateToAllEvents()
                    snackbarHostState.showSnackbar(repositories.appContext.getString(R.string.event_create_success))
                }
            } else {
                snackbarHostState.showSnackbar(repositories.appContext.getString(R.string.event_validate_error))
            }
        }
    }

    fun searchEvents(globalSearch: Boolean = false) {
        viewModelScope.launch {
            Log.d("event selection", eventSelection.toString())
            if (globalSearch) {
                clearFastTags()
                if (query.isNotBlank()) {
                    eventSelection = eventSelection.copy(tags = (eventSelection.tags)+listOf(query))
                }
                val response = repositories.eventRepository.getGlobalEvents(eventSelection = eventSelection)
                if (afterCheckResponse(response)) {
                    events = response!!.data!!
                    eventsVisible = events
                    snackbarHostState.showSnackbar("Найдено ${response.data!!.size} мероприятий")
                } else {
                    snackbarHostState.showSnackbar("Мероприятия не найдены")
                }
            } else {
                updateEvents().join()
                if (events!=null) {
                    eventsVisible = events!!.filter {
                        (it.tags.containsAll(eventSelection.tags) || eventSelection.tags.isEmpty()) &&
                        it.price >= (eventSelection.minimalPrice ?: 0) && it.price <= (eventSelection.maximalPrice ?: it.price) &&
                        it.minimalAge<=(eventSelection.age?:it.minimalAge) && (it.maximalAge?:9999)>=(eventSelection.age?:it.maximalAge?:9999) &&
                        ((query in it.name) || (query in it.description))
                    }.sortedBy { it.startTime }
                    if (eventSelection.sortBy=="далёкие") {
                        eventsVisible = eventsVisible!!.reversed()
                    }
                    snackbarHostState.showSnackbar("Найдено ${eventsVisible!!.size} мероприятий")
                }
            }
        }
    }

    fun clearSearch() {
        viewModelScope.launch {
            updateEvents()
            clearFastTags()
        }
    }

    fun deleteEvent(eventID: Int) {
        viewModelScope.launch {
            afterCheckResponse(repositories.eventRepository.deteleEvent(eventID)) {
                navigator.clearNavigate(AllEventsScreenDestination)
                snackbarHostState.showSnackbar("Мероприятие удалено")
            }
        }
    }
}