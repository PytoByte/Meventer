package pachmp.meventer.components.mainmenu.components.profile.components

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.CreateUserScreenDestination
import pachmp.meventer.components.destinations.ProfileScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserUpdate
import pachmp.meventer.data.DTO.UserUpdateEmail
import pachmp.meventer.data.DTO.UserUpdatePassword
import pachmp.meventer.data.repository.Repositories
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {

    val codeDialogVisible = mutableStateOf(false)
    var user by mutableStateOf<User?>(null)

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var code by mutableStateOf("")
    var name by mutableStateOf("")
    var nickname by mutableStateOf("")
    var birthday by mutableStateOf(LocalDate.now())

    var parentSnackbarHostState by mutableStateOf(snackbarHostState)

    var avatarUriCurrent by mutableStateOf<Uri?>(null)
        private set

    var avatarUri by mutableStateOf<Uri?>(null)
        private set

    init {
        viewModelScope.launch {
            val response = repositories.userRepository.getUserData()
            if (checkResponse(response)) {
                user = response!!.data
            }

            email = user!!.email
            name = user!!.name
            nickname = user!!.nickname
            birthday = user!!.dateOfBirth
            avatarUriCurrent = repositories.userRepository.getFileURL(user!!.avatar).toUri()
        }
    }

    fun updateAvatarUri(newAvatarURI: Uri?) {
        if (newAvatarURI != null) {
            avatarUri = newAvatarURI
        }
    }

    fun sendCode() {
        viewModelScope.launch {
            val response = repositories.userRepository.sendEmailCode(email)
            if (checkResultResponse(response = response)) {
                codeDialogVisible.value = true
            }
        }
    }

    fun updateUserData() {
        viewModelScope.launch {
            if (nickname.isEmpty() || name.isEmpty()) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены")
            } else {
                val response =
                    repositories.userRepository.updateUserData(
                        UserUpdate(nickname, name),
                        avatar = avatarUri?.let { cacheFile(it, "avatar") })
                if (checkResultResponse(response = response)) {
                    coroutineScope {
                        parentSnackbarHostState.showSnackbar("Сохранено")
                    }
                    navigator.clearNavigate(ProfileScreenDestination)
                }
            }
        }
    }

    fun updateUserEmail() {
        viewModelScope.launch {
            if (code.toIntOrNull() == null || email.isEmpty()) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены")
            } else {
                val response =
                    repositories.userRepository.updateUserEmail(UserUpdateEmail(code, email))
                if (checkResultResponse(response = response)) {
                    coroutineScope {
                        parentSnackbarHostState.showSnackbar("Сохранено")
                    }
                    navigator.clearNavigate(ProfileScreenDestination)
                }
            }
        }
    }

    fun updateUserPassword() {
        viewModelScope.launch {
            if (code.toIntOrNull() == null || password.isEmpty()  || password.length < 8 || password.length > 128) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены или заполненны неверно")
            } else {
                val response = repositories.userRepository.updateUserPassword(
                    UserUpdatePassword(
                        code,
                        password
                    )
                )
                if (checkResultResponse(response = response)) {
                    coroutineScope {
                        parentSnackbarHostState.showSnackbar("Сохранено")
                    }
                    navigator.clearNavigate(ProfileScreenDestination)
                }
            }
        }
    }

}