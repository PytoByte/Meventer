package pachmp.meventer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.auth.login.LoginViewModel
import pachmp.meventer.components.auth.register.RegisterViewModel
import pachmp.meventer.data.repository.Repositories
import pachmp.meventer.ui.theme.MeventerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @Nav
    lateinit var navigator: Navigator

    @Inject
    @RootNav
    lateinit var rootNavigator: Navigator

    @Inject
    lateinit var repositories: Repositories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            rootNavigator.setController(rememberNavController())

            val registerViewModel: RegisterViewModel = hiltViewModel()
            val loginViewModel: LoginViewModel = hiltViewModel()
            var uiState by remember { mutableStateOf(false) }
            var textState by remember { mutableStateOf("") }

            if (uiState) {
                MeventerTheme() {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        navController = rootNavigator.getController()!!,
                        modifier = Modifier.fillMaxSize(),
                        startRoute = if (repositories.encryptedSharedPreferences.getString("token", null)==null) NavGraphs.login else NavGraphs.mainmenu,
                        dependenciesContainerBuilder = {
                            dependency(NavGraphs.register) {
                                registerViewModel
                            }

                            dependency(NavGraphs.login) {
                                loginViewModel
                            }
                        }
                    )
                }
            } else {
                MeventerTheme {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(250.dp),
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "logo"
                        )
                        Text(textState)
                    }
                }

                LaunchedEffect(Unit) {
                    val response = repositories.userRepository.verifyToken()
                    if (response==null) {
                        textState = "Сервер не отвечает"
                    } else if (response.result.value!=200) {
                        Log.d("TOKEN FIRST", repositories.encryptedSharedPreferences.getString("token", "")!!)
                        repositories.encryptedSharedPreferences.edit().putString("token", null).apply()
                        uiState = true
                    } else {
                        uiState = true
                    }
                }
            }
        }
    }
}