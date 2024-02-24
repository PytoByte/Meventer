package pachmp.meventer.ui.screens.gates.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pachmp.meventer.ui.MessageModel
import pachmp.meventer.repository.DatabaseRepository
import pachmp.meventer.ui.screens.NavGraphs

class LoginViewModel(val navController: NavController): ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var messageShow by mutableStateOf(false)
        private set

    private val _messageState = MutableStateFlow(MessageModel())
    val messageState: StateFlow<MessageModel> = _messageState.asStateFlow()

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun hideError() {
        messageShow = false
    }

    fun loginRequest() {
        viewModelScope.launch {
            val tokenResponse = DatabaseRepository().login(email, password)
            if (tokenResponse.data != null) {
                // TODO: сохранение токена
                navController.navigate(NavGraphs.mainmenu)
            } else {
                _messageState.value = MessageModel("Ошибка", "Неверный логин или пароль")
                messageShow = true
            }
        }
    }

    fun navigateToRegister() {
        navController.navigate(NavGraphs.register)
    }
}