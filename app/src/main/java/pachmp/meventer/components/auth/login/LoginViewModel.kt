package pachmp.meventer.components.auth.login

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.data.DTO.UserLogin
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(@RootNav navigator: Navigator, encryptedSharedPreferences: SharedPreferences):
    DefaultViewModel(navigator, encryptedSharedPreferences) {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun loginRequest() {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                snackbarHostState.showSnackbar(message = "Поля не заполнены")
            } else {
                val tokenResponse = repository.login(UserLogin(email = email, password = password))
                if (checkResponse(response = tokenResponse.result)) {
                    val token = tokenResponse.data
                    encryptedSharedPreferences.edit().putString("token", token).apply()
                    navigator.clearNavigate(NavGraphs.mainmenu)
                }
            }
        }
    }

    fun navigateToRegister() {
        navigator.clearNavigate(NavGraphs.register)
    }
}