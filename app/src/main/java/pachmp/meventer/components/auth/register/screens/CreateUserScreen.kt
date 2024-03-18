package pachmp.meventer.components.auth.register.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import DialogDatePicker
import androidx.compose.runtime.derivedStateOf
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import pachmp.meventer.ui.transitions.FadeTransition
import pachmp.meventer.R
import pachmp.meventer.components.auth.register.RegisterViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RegisterNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun CreateUserScreen(registerViewModel: RegisterViewModel) {
    with(registerViewModel) {
        val focusManager = LocalFocusManager.current
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            registerViewModel.updateAvatarUri(it)
        }
        var passwordVisibleState by remember { mutableStateOf(false) }
        val dateDialogState = rememberMaterialDialogState()
        val formattedBirthday by remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd MMM yyyy")
                    .withLocale(Locale.getDefault())
                    .format(birthday)
            }
        }

        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Ок")
                negativeButton(text = "Отмена")
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Выберите дату",
                allowedDateValidator = {
                    it<=LocalDate.now()
                },
                locale = Locale.getDefault()
            ) {
                birthday = it
            }
        }

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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
            ) {
                Image(
                    modifier = Modifier.size(250.dp),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo"
                )
                Text("Создание пользователя")
                Spacer(modifier = Modifier.padding(2.dp))
                Text("Аватар")
                AsyncImage(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUri).build(),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop)
                Button(onClick = { launcher.launch(arrayOf("image/*")) }) {
                    Text(text = "Выбрать файл")
                }

                OutlinedTextField(
                    value = registerViewModel.nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Ник") },
                    singleLine = true,
                    supportingText = { Text(text = "Результат: @${nickname}") }
                )

                OutlinedTextField(
                    value = registerViewModel.name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.onFocusEvent { state ->
                        if (state.isFocused) {
                            dateDialogState.show()
                            focusManager.clearFocus()
                        }
                    },
                    value = formattedBirthday,
                    onValueChange = {},
                    label = { Text("Дата рождения") },
                    singleLine = true,
                    enabled = true,
                    readOnly = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
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
                            val description =
                                if (passwordVisibleState) "Hide password" else "Show password"
                            Icon(
                                imageVector = if (passwordVisibleState) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = description,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
                Button(onClick = {
                    createUser()
                }) {
                    Text("Готово")
                }
            }
        }
    }
}