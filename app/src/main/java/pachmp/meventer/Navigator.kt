package pachmp.meventer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Qualifier

class Navigator {

    var navController by mutableStateOf<NavHostController?>(null)

    fun navigate(direction: Direction, navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
        with(navController ?: throw Exception("navController didn't set")) {
            navigate(direction, navOptionsBuilder)
        }
    }


    fun clearNavigate(direction: Direction, navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
        with(navController ?: throw Exception("navController didn't set")) {
            popBackStack()
            navigate(direction, navOptionsBuilder)
        }
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RootNav

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Nav
