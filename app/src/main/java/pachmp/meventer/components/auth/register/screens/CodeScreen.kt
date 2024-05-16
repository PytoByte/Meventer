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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.auth.register.RegisterViewModel
import pachmp.meventer.data.validators.UserValidator
import pachmp.meventer.ui.transitions.FadeTransition

@RegisterNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun CodeScreen(registerViewModel: RegisterViewModel) {
    with(registerViewModel) {
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
                Text(stringResource(R.string.email_code_request))
                var codeIsError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = registerViewModel.code,
                    onValueChange = { code = it; codeIsError=!UserValidator().codeValidate(it) },
                    label = { Text(stringResource(R.string.code)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = codeIsError,
                    supportingText = {
                        if (codeIsError) {
                            Text(stringResource(R.string.field_filled_wrong))
                        }
                    }
                )

                Button(onClick = { registerViewModel.confirmRegister() }) {
                    Text(stringResource(id = R.string.next))
                }
            }
        }
    }
}