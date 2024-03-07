package pachmp.meventer

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.DestinationStyle
import pachmp.meventer.ui.screens.NavGraphs
import pachmp.meventer.ui.screens.destinations.LoginScreenDestination
import pachmp.meventer.ui.screens.destinations.MainmenuScreenDestination
import pachmp.meventer.ui.screens.gates.login.LoginViewModel
import pachmp.meventer.ui.screens.gates.register.RegisterViewModel
import pachmp.meventer.ui.screens.mainmenu.MainmenuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val encryptedSharedPreferences = EncryptedSharedPreferences.create(
            "token",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setContent {
            val navController = rememberNavController()

            val registerViewModel = viewModel<RegisterViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RegisterViewModel(navController, encryptedSharedPreferences) as T
                    }
                }
            )

            val loginViewModel = viewModel<LoginViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(navController, encryptedSharedPreferences) as T
                    }
                }
            )

            val mainmenuViewModel = viewModel<MainmenuViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainmenuViewModel(navController) as T
                    }
                }
            )

            DestinationsNavHost(navGraph = NavGraphs.root,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                dependenciesContainerBuilder = {


                    dependency(NavGraphs.register) {
                        registerViewModel
                    }

                    dependency(NavGraphs.login) {
                        loginViewModel
                    }

                    dependency(NavGraphs.mainmenu) {
                        mainmenuViewModel
                    }
                },
                startRoute = if (encryptedSharedPreferences.getString("token", null)==null) NavGraphs.login else NavGraphs.mainmenu
            )
        }
    }
}

object ProfileTransitions : DestinationStyle.Animated {
    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return fadeIn()
    }
}