package pachmp.meventer.components.mainmenu.components.profile

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
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.destinations.ProfileEditDestination
import pachmp.meventer.components.destinations.ProfileScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var user by mutableStateOf<User?>(null)
        private set

    var avatar by mutableStateOf<String?>(null)
        private set

    fun updateProfile() {
        viewModelScope.launch {
            val response = repositories.userRepository.getUserData()
            if (checkResponse(response)) {
                user = response!!.data
                avatar = repositories.userRepository.getFileURL(user!!.avatar)
            }
        }
    }

    fun logout() {
        repositories.encryptedSharedPreferences.edit().clear().apply()
        rootNavigator.clearNavigate(NavGraphs.login)
    }

    fun navigateToEdit() {
        navigator.clearNavigate(ProfileEditDestination)
    }

    fun navigateToProfile() {
        navigator.clearNavigate(ProfileScreenDestination)
    }
}