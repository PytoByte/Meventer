package pachmp.meventer.components.mainmenu.components.profile.components.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.components.mainmenu.components.profile.components.ProfileEditViewModel
import pachmp.meventer.components.mainmenu.components.profile.screens.ProfileNavGraph
import pachmp.meventer.ui.transitions.FadeTransition

@ProfileNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun PasswordEditScreen(
    profileViewModel: ProfileViewModel,
    profileEditViewModel: ProfileEditViewModel = hiltViewModel(),
) {
    with(profileEditViewModel) {
        parentSnackbarHostState = profileViewModel.snackbarHostState
        if (user != null) {
            val showCalendar = remember { mutableStateOf(false) }
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                    updateAvatarUri(it)
                }

            Scaffold(
                snackbarHost = { SnackbarHost(parentSnackbarHostState) },
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { profileViewModel.navigateToProfile() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "back"
                            )
                        }
                    }
                }
            ) { paddingValues ->
                codeDialog(codeDialogVisible, profileEditViewModel) {
                    updateUserPassword()
                }
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    Text("Изменение пароля", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.size(5.dp))
                    var passwordVisibleState by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingText = { Text("длина от 8 до 128 символов") },
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
                        sendCode()
                    }) {
                        Text("Сохранить")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Загрузка")
            }
        }
    }
}