package pachmp.meventer.components.auth.register

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.CalendarDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.destinations.CodeScreenDestination
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserRegister
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.CreateUserScreenDestination
import pachmp.meventer.components.destinations.RegisterScreenDestination
import pachmp.meventer.data.repository.Repositories
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(@RootNav navigator: Navigator, repositories: Repositories):
    DefaultViewModel(navigator, repositories) {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var code by mutableStateOf("")
    var name by mutableStateOf("")
    var nickname by mutableStateOf("")
    var birthday by mutableStateOf(LocalDate.now())

    var avatarUri by mutableStateOf<Uri?>(null)
        private set

    fun updateAvatarUri(newAvatarURI: Uri?) {
        if (newAvatarURI!=null) {
            avatarUri = newAvatarURI
        }
    }

    fun registerRequest() {
        viewModelScope.launch {
            if (email.isEmpty() || !Regex("""([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9_-]+)""").matches(email)) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены или заполнены неверно")
            } else {
                val response = repositories.userRepository.sendEmailCode(email)
                if (checkResultResponse(response = response)) {
                    navigator.clearNavigate(CodeScreenDestination())
                }
            }
        }
    }

    fun confirmRegister() {
        viewModelScope.launch {
            if (code.toIntOrNull() == null) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены")
            } else {
                val response = repositories.userRepository.verifyEmailCode(UserEmailCode(email = email, code = code))
                if (checkResultResponse(response = response)) {
                    navigator.clearNavigate(CreateUserScreenDestination())
                }
            }
        }
    }

    fun createUser() {
        viewModelScope.launch {
            if (nickname.isEmpty() || password.isEmpty() || password.length < 8 || password.length > 128) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены или заполнены неверно")
            } else {
                val tokenResponse = repositories.userRepository.register(
                    UserRegister(
                        code = code,
                        email = email,
                        password = password,
                        nickname = nickname,
                        name = name,
                        dateOfBirth = birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    ),
                    if (avatarUri!=null) cacheFile(avatarUri!!, "avatar") else null
                )

                if (checkResponse(response = tokenResponse)) {
                    val token = tokenResponse!!.data
                    repositories.encryptedSharedPreferences.edit().putString("token", token).apply()
                    navigator.clearNavigate(NavGraphs.mainmenu)
                }
            }
        }
    }

    fun navigateToLogin() {
        navigator.clearNavigate(NavGraphs.login)
    }


    fun cancelRegister() {
        password = ""
        code = ""
        name = ""
        nickname = ""
        birthday = LocalDate.now()
        avatarUri = null
        navigator.clearNavigate(RegisterScreenDestination)
    }
}

