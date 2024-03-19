package pachmp.meventer.components.mainmenu.components.profile.components.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.components.mainmenu.components.profile.components.ProfileEditViewModel
import pachmp.meventer.components.mainmenu.components.profile.screens.Avatar
import pachmp.meventer.components.mainmenu.components.profile.screens.ProfileNavGraph
import pachmp.meventer.components.widgets.TextCom
import pachmp.meventer.ui.transitions.FadeTransition


@ProfileNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun ProfileEdit(
    profileViewModel: ProfileViewModel,
    profileEditViewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        profileEditViewModel.updateAvatarUri(it)
    }

    with(profileEditViewModel) {
        parentSnackbarHostState = profileViewModel.snackbarHostState
        if (user != null) {
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
                            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back")
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    Avatar(model = if (avatarUri==null) avatarUriCurrent.toString() else avatarUri.toString())
                    Button(onClick = { launcher.launch(arrayOf("image/*")) }) {
                        Text("Изменить аватар")
                    }
                    TextCom(label = "Ник", value = nickname) {nickname = it}
                    TextCom(label = "Имя", value = name) {name = it}
                    
                    Button(onClick = {updateUserData()}) {
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

@Composable
fun codeDialog(visible: MutableState<Boolean>, profileEditViewModel: ProfileEditViewModel, check: () -> Unit) {
    if (visible.value) {
        with(profileEditViewModel) {
            Dialog(onDismissRequest = { visible.value = false }) {
                Card {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Введите код, отправленный на почту")
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Код") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Button(onClick = { check() }) {
                            Text("Готово")
                        }
                    }
                }
            }
        }
    }
}