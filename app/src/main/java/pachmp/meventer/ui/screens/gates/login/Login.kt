package pachmp.meventer.ui.screens.gates.login

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavArgs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import pachmp.meventer.ProfileTransitions
import pachmp.meventer.ui.MessageDialog
import pachmp.meventer.R
import pachmp.meventer.ui.MessageModel
import pachmp.meventer.ui.dialogDatePicker
import java.lang.Integer.max
import java.lang.Integer.min
import java.time.YearMonth

@RootNavGraph(start = true)
@NavGraph
annotation class LoginNavGraph(
    val start: Boolean = false
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@LoginNavGraph(start = true)
@Composable
@Destination(style = ProfileTransitions::class)
fun LoginScreen(loginViewModel: LoginViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(loginViewModel.snackbarHostState) }
    ) { _ ->
        Column(
            modifier = Modifier.fillMaxSize(),
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
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
}