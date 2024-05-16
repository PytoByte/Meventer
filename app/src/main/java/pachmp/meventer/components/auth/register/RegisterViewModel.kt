package pachmp.meventer.components.auth.register

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Navigator
import pachmp.meventer.R
import pachmp.meventer.RootNav
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.destinations.CodeScreenDestination
import pachmp.meventer.components.destinations.CreateUserScreenDestination
import pachmp.meventer.components.destinations.RegisterScreenDestination
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserRegister
import pachmp.meventer.data.repository.Repositories
import pachmp.meventer.data.validators.UserValidator
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
            if (!UserValidator().emailValidate(email)) {
                snackBarHostState.showSnackbar(message = repositories.appContext.getString(R.string.fields_empty_or_validate_error))
            } else {
                afterCheckResponse(repositories.userRepository.sendEmailCode(email)) {
                    navigator.clearNavigate(CodeScreenDestination())
                }
            }
        }
    }

    fun confirmRegister() {
        viewModelScope.launch {
            if (!UserValidator().codeValidate(code)) {
                snackBarHostState.showSnackbar(message = repositories.appContext.getString(R.string.fields_empty_or_validate_error))
            } else {
                afterCheckResponse(repositories.userRepository.verifyEmailCode(UserEmailCode(email = email, code = code))) {
                    navigator.clearNavigate(CreateUserScreenDestination())
                }
            }
        }
    }

    fun createUser() {
        viewModelScope.launch {
            val userRegister = UserRegister(
                code = code,
                email = email,
                password = password,
                nickname = nickname,
                name = name,
                dateOfBirth = birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )

            if (!UserValidator().userRegisterValidate(userRegister)) {
                snackBarHostState.showSnackbar(message = repositories.appContext.getString(R.string.fields_empty_or_validate_error))
            } else {
                val tokenResponse = repositories.userRepository.register(
                    userRegister,
                    if (avatarUri!=null) cacheFile(avatarUri!!, "avatar") else null
                )

                afterCheckResponse(tokenResponse) { response ->
                    val token = response.data
                    repositories.encryptedSharedPreferences.edit().putString("token", token).apply()
                    clearRegister()
                    navigator.clearNavigate(NavGraphs.mainmenu)
                }
            }
        }
    }

    fun navigateToLogin() {
        navigator.clearNavigate(NavGraphs.login)
    }

    private fun clearRegister() {
        password = ""
        code = ""
        name = ""
        nickname = ""
        birthday = LocalDate.now()
        avatarUri = null
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

