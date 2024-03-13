package pachmp.meventer.components.auth.register.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import dialogDatePicker
import pachmp.meventer.ui.transitions.FadeTransition
import pachmp.meventer.R
import pachmp.meventer.components.auth.register.RegisterViewModel

@RegisterNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun CreateUserScreen(registerViewModel: RegisterViewModel) {
    val showCalendar = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    dialogDatePicker(
        showCalendar
    ) { registerViewModel.updateBirthday(it) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    modifier = Modifier.padding(2.dp),
                    onClick = { registerViewModel.cancelRegister() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "cancel register",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(registerViewModel.snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            Image(
                modifier = Modifier.size(250.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo"
            )
            Text("Заполните все поля")

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
            var passwordVisibleState by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = registerViewModel.password,
                onValueChange = { registerViewModel.updatePassword(it) },
                label = { Text("Пароль") },
                singleLine = true,
                supportingText = { Text("длина от 8 до 128 символов") },
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
            Button(onClick = {
                registerViewModel.createUser()
            }) {
                Text("Готово")
            }
        }
    }
}