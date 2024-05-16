package pachmp.meventer.components.auth.login.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.auth.login.LoginViewModel
import pachmp.meventer.ui.transitions.FadeTransition


@LoginNavGraph(start = true)
@Composable
@Destination(style = FadeTransition::class)
fun LoginScreen(loginViewModel: LoginViewModel = hiltViewModel()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(loginViewModel.snackBarHostState) }
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
            Text(stringResource(R.string.sigh_in))
            Column {

            }
            OutlinedTextField(
                value = loginViewModel.email,
                onValueChange = { loginViewModel.updateEmail(it) },
                label = { Text(stringResource(R.string.email)) },
                singleLine = true
            )

            var passwordVisibleState by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.updatePassword(it) },
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisibleState) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        modifier = Modifier.padding(2.dp),
                        onClick = { passwordVisibleState = passwordVisibleState.not() }
                    ) {
                        val description = if (passwordVisibleState) "Hide password" else "Show password"
                        Icon(
                            imageVector = if (passwordVisibleState) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = description,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )

            Button(modifier = Modifier.padding(top = 10.dp),
                onClick = { loginViewModel.loginRequest() }
            ) {
                Text(stringResource(R.string.sigh_in))
            }

            OutlinedButton(
                modifier = Modifier.padding(top = 10.dp, bottom = 25.dp),
                onClick = { loginViewModel.navigateToRegister() }
            ) {
                Text(stringResource(R.string.i_dont_have_an_account))
            }
        }
    }
}