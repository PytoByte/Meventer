package pachmp.meventer.components.auth.register.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import pachmp.meventer.R
import pachmp.meventer.components.auth.register.RegisterViewModel
import pachmp.meventer.data.validators.UserValidator
import pachmp.meventer.ui.transitions.FadeTransition
import java.time.format.DateTimeFormatter
import java.util.Locale

@RegisterNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun CreateUserScreen(registerViewModel: RegisterViewModel) {
    with(registerViewModel) {
        val focusManager = LocalFocusManager.current

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                registerViewModel.updateAvatarUri(uri)
            }
        )

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
                positiveButton(text = stringResource(R.string.ok))
                negativeButton(text = stringResource(R.string.cancel))
            }
        ) {
            datepicker(
                initialDate = birthday,
                title = stringResource(R.string.choose_date),
                allowedDateValidator = {
                    UserValidator().birthdayValidate(it)
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
                Text(stringResource(R.string.creating_user))
                Spacer(modifier = Modifier.padding(2.dp))
                Text(stringResource(R.string.avatar))
                AsyncImage(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUri).build(),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop
                )
                Button(onClick = {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(text = stringResource(R.string.choose_file))
                }

                var nickIsError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = registerViewModel.nickname,
                    onValueChange = { nickname = it; nickIsError = !UserValidator().nickValidate(it) },
                    label = { Text(stringResource(R.string.nick)) },
                    singleLine = true,
                    isError = nickIsError,
                    supportingText = {
                        Text(text = stringResource(R.string.nick_preview, nickname))
                        if (nickIsError) {
                            Text(stringResource(R.string.field_filled_wrong))
                        }
                    }
                )

                var nameIsError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = registerViewModel.name,
                    onValueChange = { name = it; nameIsError = !UserValidator().nameValidate(it)},
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true,
                    isError = nameIsError,
                    supportingText = {
                        if (nickIsError) {
                            Text(stringResource(R.string.field_filled_wrong))
                        }
                    }
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
                    label = { Text(stringResource(R.string.birthday)) },
                    singleLine = true,
                    enabled = true,
                    readOnly = true
                )

                var passwordIsError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordIsError = !UserValidator().passwordValidate(it) },
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    supportingText = {
                        Column() {
                            Text(stringResource(R.string.password_length_hint, UserValidator().minPasswordLength))
                            if (passwordIsError) {
                                Text(stringResource(R.string.field_filled_wrong))
                            }
                        }
                                     },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisibleState) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordIsError,
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
                    Text(stringResource(R.string.ready))
                }
            }
        }
    }
}