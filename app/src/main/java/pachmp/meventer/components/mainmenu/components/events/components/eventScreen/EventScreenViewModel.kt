package pachmp.meventer.components.mainmenu.components.events.components.eventScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.NullableUserID
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class EventScreenViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var ready by mutableStateOf<Boolean?>(null)
    var event by mutableStateOf<Event?>(null)
    var appUser by mutableStateOf<User?>(null)
    var originator by mutableStateOf<User?>(null)
    var organizers by mutableStateOf<List<User?>?>(null)
    var participants by mutableStateOf<List<User?>?>(null)

    fun init(event: Event, appUser: User) {
        this.event = event
        this.appUser = appUser
        viewModelScope.launch {
            val originatorRequest = repositories.userRepository.getUserData(NullableUserID(id = event.originator))
            if (checkResponse(originatorRequest)) {
                originator = fixUserAvatar(originatorRequest!!.data!!)
            }

            organizers = List<User?>(event.organizers.size) {
                val userRequest = repositories.userRepository.getUserData(NullableUserID(id = event.organizers[it]))
                if (checkResponse(originatorRequest)) {
                    fixUserAvatar(originatorRequest!!.data!!)
                } else {
                    null
                }
            }


            /*participants = List<User?>(event.organizers.size) {
                val userRequest = repositories.userRepository.getUserData(NullableUserID(id = event.[it]))
                if (checkResponse(originatorRequest)) {
                    originatorRequest!!.data!!
                } else {
                    null
                }
            }*/

            ready = true
        }
    }
}