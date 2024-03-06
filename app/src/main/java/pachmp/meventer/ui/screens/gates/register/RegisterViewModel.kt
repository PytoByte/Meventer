package pachmp.meventer.ui.screens.gates.register

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import com.kizitonwose.calendar.core.CalendarDay
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pachmp.meventer.ui.MessageModel
import pachmp.meventer.repository.DatabaseRepository
import pachmp.meventer.repository.UserEmailCode
import pachmp.meventer.repository.UserRegister
import pachmp.meventer.ui.screens.NavGraphs
import pachmp.meventer.ui.screens.destinations.CodeScreenDestination
import pachmp.meventer.ui.screens.destinations.CreateUserScreenDestination

class RegisterViewModel(val navController: NavController, val encryptedSharedPreferences: SharedPreferences): ViewModel() {

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

    val snackbarHostState = SnackbarHostState()

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
        birthday = "%04d-%02d-%02d".format(newBirthday.date.year, newBirthday.date.month.value, newBirthday.date.dayOfMonth)
        Log.d("newClendarDay", birthday)
    }

    fun registerRequest() {
        viewModelScope.launch {
            if (email.isEmpty() || !Regex("""([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9_-]+)""").matches(email)) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны или заполненны неверно")
            } else {
                val response = DatabaseRepository().sendEmailCode(email)
                if (response.statusCode == 200.toShort()) {
                    navController.navigate(CodeScreenDestination())
                } else {
                    snackbarHostState.showSnackbar(message = response.message)
                }
            }
        }
    }

    fun confirmRegister() {
        viewModelScope.launch {
            if (code.toIntOrNull()==null) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны")
            } else {
                val response = DatabaseRepository().verifyEmailCode(UserEmailCode(email = email, code = code))
                if (response.statusCode==200.toShort()) {
                    navController.navigate(CreateUserScreenDestination())
                } else {
                    snackbarHostState.showSnackbar(message = response.message)
                }
            }
        }
    }

    fun createUser() {
        viewModelScope.launch {
            if (nickname.isEmpty() || birthday.isEmpty() || password.isEmpty() || password.length<8 || password.length>128) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны или заполненны неверно")
            } else {
                val tokenResponse = DatabaseRepository().register(UserRegister(code = code, email = email, password = password, nickname = nickname, dateOfBirth = birthday))
                if (tokenResponse.result.statusCode == 200.toShort()) {
                    val token = tokenResponse.data!!
                    encryptedSharedPreferences.edit().putString("token", token).apply()
                    navController.navigate(NavGraphs.mainmenu)
                } else {
                    snackbarHostState.showSnackbar(message = tokenResponse.result.message)
                }
            }
        }
    }

    fun navigateToLogin() {
        navController.navigate(NavGraphs.login)
    }
}

