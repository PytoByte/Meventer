package pachmp.meventer.components.mainmenu.components.profile.components.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.profile.ProfileNavGraph
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.components.mainmenu.components.profile.components.ProfileEditViewModel
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.components.widgets.TextCom
import pachmp.meventer.ui.transitions.FadeTransition

@ProfileNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun EmailEditScreen(
    profileViewModel: ProfileViewModel,
    profileEditViewModel: ProfileEditViewModel = hiltViewModel(),
) {
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
                CodeDialog(codeDialogVisible, profileEditViewModel) {
                    updateUserEmail()
                }
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    Image(modifier=Modifier.size(250.dp), painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
                    Text(stringResource(R.string.email_changing), style = MaterialTheme.typography.titleMedium)
                    TextCom(label = stringResource(R.string.email), value = email) {email = it}
                    Button(onClick = {
                        sendCode()
                    }) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        } else {
            LoadingScreen(Modifier.fillMaxSize())
        }
    }
}