package pachmp.meventer.components.auth.register.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import pachmp.meventer.ui.transitions.FadeTransition
import pachmp.meventer.R
import pachmp.meventer.components.auth.register.RegisterViewModel


@RootNavGraph
@NavGraph
annotation class RegisterNavGraph(
    val start: Boolean = false
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RegisterNavGraph(start = true)
@Destination(style = FadeTransition::class)
@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel = hiltViewModel()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(registerViewModel.snackbarHostState) }
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
}