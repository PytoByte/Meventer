package pachmp.meventer.components.mainmenu.components.events.components.eventEditor

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.repository.Repositories
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
    repositories: Repositories
) : BottomViewModel(rootNavigator, navigator, repositories)  {
    var parentSnackbarHostState: SnackbarHostState? = null

    var title by mutableStateOf("")
    var price by mutableStateOf("")
    var maxAge by mutableStateOf("")
    var minAge by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedImageUris by mutableStateOf(emptyList<Uri>())
        private set
    var pickedDate by mutableStateOf(LocalDate.now())
    var pickedTime by mutableStateOf(LocalTime.now())
    var selectedCategories by mutableStateOf(setOf<String>())

    fun extendSelectedImageUris(uris: List<Uri>) {
        this.selectedImageUris+=uris
    }

    fun removeSelectedImageUris(uriID: Int) {
        selectedImageUris=selectedImageUris.subList(0,uriID)+selectedImageUris.subList(uriID+1,selectedImageUris.size)
    }

    fun getEventCreate(): EventCreate? {
        if (title.isEmpty() || LocalDateTime.of(pickedDate, pickedTime)<=LocalDateTime.now()) {
            return null
        } else {
            return EventCreate(
                name = title,
                description = description,
                startTime = LocalDateTime.of(pickedDate, pickedTime).atOffset(ZoneOffset.systemDefault().rules.getOffset(Instant.now())).toInstant(),
                minimalAge = if (minAge.isEmpty()) null else minAge.toShort(),
                maximalAge = if (maxAge.isEmpty()) null else maxAge.toShort(),
                price = if (price.isEmpty()) null else price.toInt(),
                tags = selectedCategories.toList())
        }
    }
}