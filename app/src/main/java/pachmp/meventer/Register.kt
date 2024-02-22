package pachmp.meventer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.runBlocking
import pachmp.meventer.destinations.CodeScreenDestination
import pachmp.meventer.destinations.CreateUserScreenDestination

@NavGraph
annotation class RegisterNavGraph(
    val start: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@RegisterNavGraph(start = true)
@Destination
@Composable
fun RegisterScreen(navigator: DestinationsNavigator) {
    val login = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
        Image(modifier= Modifier.size(250.dp), painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        Text("Регистрация")
        TextField(
            value = login.value,
            onValueChange = {newValue -> login.value = newValue},
            label = {Text("Логин")},
            singleLine = true
        )
        TextField(
            value = password.value,
            onValueChange = {newValue -> password.value = newValue},
            label = {Text("Пароль")},
            singleLine = true
        )
        Button(onClick = {
            runBlocking {
                if (Database().registerRequest(login.value, password.value)) {
                    navigator.navigate(CodeScreenDestination())
                } else {
                    // TODO: Вывести ошибку "почта занята"
                }
            }
        }) {
            Text("Зарегистрироваться")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RegisterNavGraph
@Destination
@Composable
fun CodeScreen(navigator: DestinationsNavigator) {
    val code = remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
        Image(modifier= Modifier.size(250.dp), painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        Text("Введите код")
        TextField(
            value = code.value,
            onValueChange = {newValue -> code.value = newValue},
            label = {Text("Код")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            runBlocking {
                if (code.value.toIntOrNull()==null) {
                    // TODO: Вывести ошибку "неверный код"
                } else {
                    if (Database().confirmRegister(code.value.toInt())) {
                        navigator.navigate(CreateUserScreenDestination())
                    }
                }
            }
        }) {
            Text("Зарегистрироваться")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RegisterNavGraph
@Destination
@Composable
fun CreateUserScreen(navigator: DestinationsNavigator) {
    val nickname = remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
        Image(modifier= Modifier.size(250.dp), painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        Text("Введите ваше имя")
        TextField(
            value = nickname.value,
            onValueChange = {newValue -> nickname.value = newValue},
            label = {Text("Имя")},
            singleLine = true
        )
        Button(onClick = {
            runBlocking {
                val tokenResponse = Database().createUser(nickname.value)
                if (tokenResponse.data!=null) {
                    // TODO: Переход на основной экран И сохранение токена
                }
            }
        }) {
            Text("Готово")
        }
    }
}