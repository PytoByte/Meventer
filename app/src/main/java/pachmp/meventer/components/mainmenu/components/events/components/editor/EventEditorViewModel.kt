package pachmp.meventer.components.mainmenu.components.events.components.editor

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.EventScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.DTO.EventUpdate
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import pachmp.meventer.data.validators.EventValidator
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class EventEditorViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var event by mutableStateOf<Event?>(null)
    var appUser by mutableStateOf<User?>(null)

    var parentSnackbarHostState: SnackbarHostState = snackbarHostState

    var title by mutableStateOf("")
    var price by mutableStateOf("")
    var maxAge by mutableStateOf("")
    var minAge by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedImageUris by mutableStateOf(emptyList<Uri>())
        private set

    private var deletedImageUris by mutableStateOf(emptyList<String>())
        private set

    var pickedDate: LocalDate by mutableStateOf(LocalDate.now())
    var pickedTime: LocalTime by mutableStateOf(LocalTime.now())
    var selectedCategories by mutableStateOf(setOf<String>())


    fun initEditor(eventID: Int, appUserID: Int) {
        viewModelScope.launch {
            val eventResponse = repositories.eventRepository.getEvent(eventID)
            if (afterCheckResponse(eventResponse)) {
                event = eventResponse!!.data
                title = event!!.name
                description = event!!.description
                price = event!!.price.toString()
                minAge = event!!.minimalAge.toString()
                maxAge = event!!.maximalAge?.toString() ?: ""
                selectedCategories = event!!.tags.toSet()
                pickedDate =
                    event!!.startTime.atOffset(ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
                        .toLocalDate()
                pickedTime =
                    event!!.startTime.atOffset(ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
                        .toLocalTime()
                selectedImageUris = fixEventImages(event!!).images.map { it.toUri() }
            } else {
                navigator.clearNavigate(EventScreenDestination)
                parentSnackbarHostState.showSnackbar("Не удалось загрузить мероприятие")
            }

            val userResponse = repositories.userRepository.getUserData(appUserID)
            if (afterCheckResponse(userResponse)) {
                appUser = userResponse!!.data!!
            } else {
                navigator.clearNavigate(EventScreenDestination)
                parentSnackbarHostState.showSnackbar("Не удалось загрузить пользователя")
            }
        }
    }

    fun extendSelectedImageUris(uris: List<Uri>) {
        this.selectedImageUris += uris
    }

    fun removeSelectedImageUris(uriID: Int) {
        if (repositories.fileRepository.isServerFile(selectedImageUris[uriID].toString())) {
            deletedImageUris =
                deletedImageUris + repositories.fileRepository.getFileName(selectedImageUris[uriID].toString())
        }
        selectedImageUris = selectedImageUris.subList(0, uriID) + selectedImageUris.subList(
            uriID + 1,
            selectedImageUris.size
        )
    }

    fun getEventCreate(): EventCreate? {
        val eventCreate = EventCreate(
            name = title,
            description = description,
            startTime = LocalDateTime.of(pickedDate, pickedTime)
                .atOffset(ZoneOffset.systemDefault().rules.getOffset(Instant.now())).toInstant(),
            minimalAge = minAge.toShortOrNull(),
            maximalAge = maxAge.toShortOrNull(),
            price = if ((price.toIntOrNull() ?: 0) > 0) price.toIntOrNull() else 0,
            tags = selectedCategories.toList()
        )

        return if (EventValidator().eventCreateValidate(eventCreate)) eventCreate else null
    }

    fun getEventUpdate(): EventUpdate? {
        val startTime = LocalDateTime.of(pickedDate, pickedTime)
            .atOffset(ZoneOffset.systemDefault().rules.getOffset(Instant.now())).toInstant()

        val eventUpdate = EventUpdate(
                eventID = event!!.id,
                name = if (event!!.name == title) null else title,
                description = if (event!!.description == description) null else description,
                startTime = if (event!!.startTime == startTime) null else startTime,
                minimalAge = if (event!!.minimalAge == (minAge.toShortOrNull()
                        ?: 0.toShort())
                ) null else minAge.toShortOrNull() ?: 0.toShort(),
                maximalAge = if (event!!.maximalAge == maxAge.toShortOrNull()) null else maxAge.toShortOrNull()
                    ?: 999.toShort(),
                price = if (event!!.price == (price.toIntOrNull() ?: 0) && (price.toIntOrNull()
                        ?: 0) < 0
                ) null else price.toIntOrNull() ?: 0,
                tags = if (event!!.tags == selectedCategories.toList()) null else selectedCategories.toList(),
                deletedImages = deletedImageUris
            )

        return if (EventValidator().evenUpdateValidate(eventUpdate)) eventUpdate else null
    }

    fun filterImages(): List<Uri> {
        return selectedImageUris.filter {
            repositories.fileRepository.isServerFile(it.toString()).not()
        }
    }
}