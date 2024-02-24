package pachmp.meventer.ui.screens.gates.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.launch
import pachmp.meventer.ui.MessageDialog
import pachmp.meventer.R
import pachmp.meventer.ui.showDatePicker

@RootNavGraph()
@NavGraph
annotation class RegisterNavGraph(
    val start: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@RegisterNavGraph(start = true)
@Destination
@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel) {
    MessageDialog(
        registerViewModel.messageShow,
        registerViewModel.messageState.collectAsState().value
    ) { registerViewModel.hideError() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )
        Text("Регистрация")

        OutlinedTextField(
            value = registerViewModel.email,
            onValueChange = { registerViewModel.updateEmail(it) },
            label = { Text("Почта") },
            singleLine = true
        )

        Button(onClick = {
            registerViewModel.registerRequest()
        }) {
            Text("Зарегистрироваться")
        }

        OutlinedButton(
            modifier = Modifier.padding(top = 10.dp, bottom = 25.dp),
            onClick = { registerViewModel.navigateToLogin() }
        ) {
            Text("У меня есть аккаунт")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RegisterNavGraph
@Destination
@Composable
fun CodeScreen(registerViewModel: RegisterViewModel) {
    MessageDialog(
        registerViewModel.messageShow,
        registerViewModel.messageState.collectAsState().value
    ) { registerViewModel.hideError() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )
        Text("Введите код")
        OutlinedTextField(
            value = registerViewModel.code,
            onValueChange = { registerViewModel.updateCode(it) },
            label = { Text("Код") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        val corscope = rememberCoroutineScope()

        Button(onClick = {
            corscope.launch {
                registerViewModel.confirmRegister()
            }
        }) {
            Text("Зарегистрироваться")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RegisterNavGraph
@Destination
@Composable
fun CreateUserScreen(registerViewModel: RegisterViewModel) {
    MessageDialog(
        registerViewModel.messageShow,
        registerViewModel.messageState.collectAsState().value
    ) { registerViewModel.hideError() }

    val showCalendar = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    showDatePicker(showCalendar, { registerViewModel.updateBirthday(it) })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )
        Text("Введите ваше имя и пароль")

        OutlinedTextField(
            value = registerViewModel.nickname,
            onValueChange = { registerViewModel.updateNickname(it) },
            label = { Text("Имя") },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier.onFocusEvent { state ->
                if (state.isFocused) {
                    showCalendar.value = true
                    focusManager.clearFocus()
                }
            },
            value = registerViewModel.birthday,
            onValueChange = {},
            label = { Text("Дата рождения") },
            singleLine = true,
            enabled = true,
            readOnly = true
        )
        OutlinedTextField(
            value = registerViewModel.password,
            onValueChange = { registerViewModel.updatePassword(it) },
            label = { Text("Пароль") },
            singleLine = true
        )
        Button(onClick = {
            registerViewModel.createUser()
        }) {
            Text("Готово")
        }
    }
}