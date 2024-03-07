package pachmp.meventer.ui.screens.gates.login

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pachmp.meventer.repository.DatabaseRepository
<<<<<<< HEAD
import pachmp.meventer.ui.MessageModel
=======
import pachmp.meventer.repository.UserLogin
>>>>>>> VV
import pachmp.meventer.ui.screens.NavGraphs

class LoginViewModel(val navController: NavController, val encryptedSharedPreferences: SharedPreferences): ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    val snackbarHostState = SnackbarHostState()

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun loginRequest() {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                snackbarHostState.showSnackbar(message = "Поля не заполненны")
            } else {
                val tokenResponse = DatabaseRepository().login(UserLogin(email = email, password = password))
                if (tokenResponse.data != null) {
                    val token = tokenResponse.data!!
                    encryptedSharedPreferences.edit().putString("token", token).apply()
                    navController.navigate(NavGraphs.mainmenu)
                } else {
                    snackbarHostState.showSnackbar(message = tokenResponse.result.message, duration = SnackbarDuration.Short)
                }
            }
        }
    }

    fun navigateToRegister() {
        navController.navigate(NavGraphs.register)
    }
}