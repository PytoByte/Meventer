package pachmp.meventer.components.auth.register

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.core.CalendarDay
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.destinations.CodeScreenDestination
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserRegister
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.components.destinations.CreateUserScreenDestination
import pachmp.meventer.components.destinations.RegisterScreenDestination
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(@Nav navigator: Navigator, encryptedSharedPreferences: SharedPreferences):
    DefaultViewModel(navigator, encryptedSharedPreferences) {
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var code by mutableStateOf("")
        private set

    var nickname by mutableStateOf("")
        private set

    var birthday by mutableStateOf("")
        private set

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun updateCode(newCode: String) {
        code = newCode
    }

    fun updateNickname(newNickname: String) {
        nickname = newNickname
    }

    fun updateBirthday(newBirthday: CalendarDay) {
        birthday = "%04d-%02d-%02d".format(
            newBirthday.date.year,
            newBirthday.date.month.value,
            newBirthday.date.dayOfMonth
        )
        Log.d("newClendarDay", birthday)
    }

    fun registerRequest() {
        viewModelScope.launch {
            if (email.isEmpty() || !Regex("""([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9_-]+)""").matches(email)) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны или заполненны неверно")
            } else {
                val response = repository.sendEmailCode(email)
                if (checkResponse(response = response)) {
                    navigator.clearNavigate(CodeScreenDestination())
                }
            }
        }
    }

    fun confirmRegister() {
        viewModelScope.launch {
            if (code.toIntOrNull() == null) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны")
            } else {
                val response = repository.verifyEmailCode(UserEmailCode(email = email, code = code))
                if (checkResponse(response = response)) {
                    navigator.clearNavigate(CreateUserScreenDestination())
                }
            }
        }
    }

    fun createUser() {
        viewModelScope.launch {
            if (nickname.isEmpty() || birthday.isEmpty() || password.isEmpty() || password.length < 8 || password.length > 128) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны или заполненны неверно")
            } else {
                val tokenResponse = repository.register(
                    UserRegister(
                        code = code,
                        email = email,
                        password = password,
                        nickname = nickname,
                        dateOfBirth = birthday
                    )
                )

                if (checkResponse(response = tokenResponse.result)) {
                    val token = tokenResponse.data
                    encryptedSharedPreferences.edit().putString("token", token).apply()
                    navigator.clearNavigate(NavGraphs.mainmenu)
                }
            }
        }
    }

    fun navigateToLogin() {
        navigator.clearNavigate(NavGraphs.login)
    }


    fun cancelRegister() {
        code = ""
        nickname = ""
        birthday = ""
        password = ""
        navigator.clearNavigate(RegisterScreenDestination)
    }
}

