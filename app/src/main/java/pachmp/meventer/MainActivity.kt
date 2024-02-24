package pachmp.meventer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import pachmp.meventer.ui.screens.NavGraphs
import pachmp.meventer.ui.screens.gates.login.LoginViewModel
import pachmp.meventer.ui.screens.gates.register.RegisterViewModel
import pachmp.meventer.ui.screens.mainmenu.MainmenuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            DestinationsNavHost(navGraph = NavGraphs.root, navController=navController, modifier = Modifier.fillMaxSize(),
                dependenciesContainerBuilder = {
                    val registerViewModel = viewModel<RegisterViewModel>(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return RegisterViewModel(navController) as T
                            }
                        }
                    )

                    dependency(NavGraphs.register) {
                        registerViewModel
                    }


                    val loginViewModel = viewModel<LoginViewModel>(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LoginViewModel(navController) as T
                            }
                        }
                    )

                    dependency(NavGraphs.login) {
                        loginViewModel
                    }

                    val mainmenuViewModel = viewModel<MainmenuViewModel>(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MainmenuViewModel(navController) as T
                            }
                        }
                    )

                    dependency(NavGraphs.mainmenu) {
                        mainmenuViewModel
                    }
                }
            )
        }
    }
}