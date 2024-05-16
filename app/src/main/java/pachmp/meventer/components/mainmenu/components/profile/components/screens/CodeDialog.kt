package pachmp.meventer.components.mainmenu.components.profile.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.profile.components.ProfileEditViewModel

@Composable
fun CodeDialog(
    visible: MutableState<Boolean>,
    profileEditViewModel: ProfileEditViewModel,
    check: () -> Unit,
) {
    if (visible.value) {
        with(profileEditViewModel) {
            Dialog(onDismissRequest = { visible.value = false }) {
                Surface(modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.verification), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(stringResource(R.string.email_code_request))
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text(stringResource(R.string.code)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Button(onClick = { check() }) {
                            Text(stringResource(R.string.ready))
                        }
                    }
                }
            }
        }
    }
}