package pachmp.meventer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.runBlocking


@RootNavGraph(start = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun LoginScreen(navigator: DestinationsNavigator) {
    val mail = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
        Image(modifier= Modifier.size(250.dp), painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        Text("Вход")
        TextField(
            value = mail.value,
            onValueChange = {newValue -> mail.value = newValue},
            label = {Text("Почта")},
            singleLine = true
        )
        TextField(
            value = password.value,
            onValueChange = {newValue -> password.value = newValue},
            label = {Text("Пароль")},
            singleLine = true
        )
        Button(modifier = Modifier.padding(top=10.dp),
            onClick = {
            runBlocking {
                val tokenResponse = Database().login(mail.value, password.value)
                if (tokenResponse.data != null) {
                    // TODO: Переход на основной экран И сохранение токена
                } else {
                    // TODO: Вывести ошибку "нет такой почты"
                }
            }
        }) {
            Text("Войти")
        }

        OutlinedButton(
            modifier = Modifier.padding(top=10.dp, bottom = 25.dp),
            onClick = {
            runBlocking {
                // TODO: Переход на экран регестрации
            }
        }) {
            Text("У меня нет аккаунта")
        }
    }
}