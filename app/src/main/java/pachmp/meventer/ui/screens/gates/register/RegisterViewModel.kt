package pachmp.meventer.ui.screens.gates.register

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
import pachmp.meventer.ui.screens.destinations.CodeScreenDestination
import pachmp.meventer.ui.screens.destinations.CreateUserScreenDestination

class RegisterViewModel(val navController: NavController): ViewModel() {
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

    fun updateCode(newCode: String) {
        code = newCode
    }

    fun updateNickname(newNickname: String) {
        nickname = newNickname
    }

    fun updateBirthday(newBirthday: String) {
        birthday = newBirthday
    }

    fun hideError() {
        messageShow = false
    }

    fun registerRequest() {
        viewModelScope.launch {
            if (DatabaseRepository().registerRequest(email)) {
                navController.navigate(CodeScreenDestination())
            } else {
                _messageState.value = MessageModel("Ошибка", "Почта занята")
                messageShow = true
            }
        }
    }

    fun confirmRegister() {
        viewModelScope.launch {
            if (code.toIntOrNull()==null) {
                _messageState.value = MessageModel("Ошибка", "Неверный код")
                messageShow = true
            } else {
                if (DatabaseRepository().confirmRegister(code.toInt())) {
                    navController.navigate(CreateUserScreenDestination())
                }
            }
        }
    }

    fun createUser() {
        viewModelScope.launch {
            val tokenResponse = DatabaseRepository().createUser(nickname, password)
            if (tokenResponse.data!=null) {
                // TODO: сохранение токена
                navController.navigate(NavGraphs.mainmenu)
            }
        }
    }

    fun navigateToLogin() {
        navController.navigate(NavGraphs.login)
    }
}

