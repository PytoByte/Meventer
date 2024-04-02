package pachmp.meventer.components.auth.register.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import pachmp.meventer.R
import pachmp.meventer.components.auth.register.RegisterViewModel
import pachmp.meventer.ui.transitions.FadeTransition


@RootNavGraph
@NavGraph
annotation class RegisterNavGraph(
    val start: Boolean = false,
)

@RequiresApi(Build.VERSION_CODES.R)
@RegisterNavGraph(start = true)
@Destination(style = FadeTransition::class)
@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel) {
    with(registerViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(registerViewModel.snackBarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
                    onValueChange = { email = it },
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
    }
}