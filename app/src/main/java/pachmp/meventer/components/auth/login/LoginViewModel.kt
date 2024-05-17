package pachmp.meventer.components.auth.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.data.DTO.UserLogin
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(@RootNav navigator: Navigator, repositories: Repositories):
    DefaultViewModel(navigator, repositories) {

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
                afterCheckResponse(repositories.userRepository.login(UserLogin(email = email, password = password)))
                { response ->
                    val token = response.data!!
                    Log.d("NEW TOKEN", token)
                    repositories.encryptedSharedPreferences.edit().putString("token", token).apply()
                    navigator.clearNavigate(NavGraphs.mainmenu)
                }
            }
        }
    }

    fun navigateToRegister() {
        navigator.clearNavigate(NavGraphs.register)
    }
}