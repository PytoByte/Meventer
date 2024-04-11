package pachmp.meventer.components.mainmenu.components.profile.components

import android.net.Uri
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
import pachmp.meventer.components.destinations.ProfileScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.User
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

    var parentSnackbarHostState by mutableStateOf(snackBarHostState)

    var avatarUriCurrent by mutableStateOf<Uri?>(null)
        private set

    var avatarUri by mutableStateOf<Uri?>(null)
        private set

    init {
        viewModelScope.launch {
            afterCheckResponse(repositories.userRepository.getUserData()) { response ->
                user = response.data
            }

            email = user!!.email
            name = user!!.name
            nickname = user!!.nickname
            birthday = user!!.dateOfBirth
            avatarUriCurrent = repositories.fileRepository.getFileURL(user!!.avatar).toUri()
        }
    }

    fun updateAvatarUri(newAvatarURI: Uri?) {
        if (newAvatarURI != null) {
            avatarUri = newAvatarURI
        }
    }

    fun sendCode() {
        viewModelScope.launch {
            afterCheckResponse(repositories.userRepository.sendEmailCode(email)) {
                codeDialogVisible.value = true
            }
        }
    }

    fun updateUserData() {
        viewModelScope.launch {
            if (nickname.isEmpty() || name.isEmpty()) {
                parentSnackbarHostState.showSnackbar(message = "Поля не заполнены")
            } else if (nickname == user!!.nickname && name == user!!.name && avatarUri == null) {
                parentSnackbarHostState.showSnackbar(message = "Нет никаких изменений")
            } else {
                val response = repositories.userRepository.updateUserData(
                    UserUpdate(
                        if (nickname == user!!.nickname) null else nickname,
                        if (name == user!!.name) null else name
                    ),
                    avatar = avatarUri?.let { cacheFile(it, "avatar") }
                )

                afterCheckResponse(response) {
                    navigator.clearNavigate(ProfileScreenDestination)
                    parentSnackbarHostState.showSnackbar("Сохранено")
                }
            }
        }
    }

    fun updateUserEmail() {
        viewModelScope.launch {
            if (code.toIntOrNull() == null || email.isEmpty()) {
                parentSnackbarHostState.showSnackbar(message = "Поля не заполнены")
            } else if (email == user!!.email) {
                parentSnackbarHostState.showSnackbar(message = "Нет никаких изменений")
            } else {
                afterCheckResponse(
                    repositories.userRepository.updateUserEmail(
                        UserUpdateEmail(
                            code,
                            email
                        )
                    )
                ) {
                    navigator.clearNavigate(ProfileScreenDestination)
                    parentSnackbarHostState.showSnackbar("Сохранено")
                }
            }
        }
    }

    fun updateUserPassword() {
        viewModelScope.launch {
            if (code.toIntOrNull() == null || password.isEmpty() || password.length < 8 || password.length > 128) {
                parentSnackbarHostState.showSnackbar(message = "Поля не заполнены или заполненны неверно")
            } else {
                afterCheckResponse(
                    repositories.userRepository.updateUserPassword(
                        UserUpdatePassword(
                            code,
                            password
                        )
                    )
                ) {
                    navigator.clearNavigate(ProfileScreenDestination)
                    parentSnackbarHostState.showSnackbar("Сохранено")
                }
            }
        }
    }

}