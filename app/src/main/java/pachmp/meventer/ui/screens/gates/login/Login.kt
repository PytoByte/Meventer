package pachmp.meventer.ui.screens.gates.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import pachmp.meventer.ui.MessageDialog
import pachmp.meventer.R

@RootNavGraph(start = true)
@NavGraph
annotation class LoginNavGraph(
    val start: Boolean = false
)

@LoginNavGraph(start = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun LoginScreen(loginViewModel: LoginViewModel) {
    MessageDialog(
        loginViewModel.messageShow,
        loginViewModel.messageState.collectAsState().value
    ) { loginViewModel.hideError() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )
        Text("Вход")
        OutlinedTextField(
            value = loginViewModel.email,
            onValueChange = { loginViewModel.updateEmail(it) },
            label = { Text("Почта") },
            singleLine = true
        )
        OutlinedTextField(
            value = loginViewModel.password,
            onValueChange = { loginViewModel.updatePassword(it) },
            label = { Text("Пароль") },
            singleLine = true
        )
        Button(modifier = Modifier.padding(top = 10.dp),
            onClick = { loginViewModel.loginRequest() }
        ) {
            Text("Войти")
        }

        OutlinedButton(
            modifier = Modifier.padding(top = 10.dp, bottom = 25.dp),
            onClick = { loginViewModel.navigateToRegister() }
        ) {
            Text("У меня нет аккаунта")
        }
    }
}