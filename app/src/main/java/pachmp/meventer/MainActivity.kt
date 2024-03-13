package pachmp.meventer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.utils.navGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.auth.login.LoginViewModel
import pachmp.meventer.components.auth.register.RegisterViewModel
import pachmp.meventer.data.DTO.ResultResponse
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

            runBlocking() {
                val response = repositories.authRepository.verifyToken()
                if (response==null) {
                    repositories.encryptedSharedPreferences.edit().putString("token", null).apply()
                } else if (response.code!=200.toShort()) {
                    repositories.encryptedSharedPreferences.edit().putString("token", null).apply()
                }
            }

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
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}