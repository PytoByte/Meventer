package pachmp.meventer

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction
import javax.inject.Qualifier

class Navigator {
    private var navController: NavHostController? = null

    fun setController(navController: NavHostController) {
        this.navController = navController
    }

    fun getController(): NavHostController? {
        return navController
    }

    fun navigate(direction: Direction, navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
        with(navController ?: throw IllegalStateException("navController didn't set")) {
            navigate(direction, navOptionsBuilder)
        }
    }


    fun clearNavigate(direction: Direction, navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
        with(navController ?: throw IllegalStateException("navController didn't set")) {
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
