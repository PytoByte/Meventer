package pachmp.meventer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import pachmp.meventer.components.NavGraphs
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @Nav
    lateinit var navigator: Navigator

    @Inject
    @RootNav
    lateinit var rootNavigator: Navigator

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
            navigator.navController = rememberNavController()
            rootNavigator.navController = rememberNavController()

            println("bruh")
            println(navigator.navController)
            println(rootNavigator.navController)

            DestinationsNavHost(navGraph = NavGraphs.root,
                navController = navigator.navController!!,
                modifier = Modifier.fillMaxSize(),
                startRoute = if (encryptedSharedPreferences.getString("token", null)==null) NavGraphs.login else NavGraphs.mainmenu
            )
        }
    }
}