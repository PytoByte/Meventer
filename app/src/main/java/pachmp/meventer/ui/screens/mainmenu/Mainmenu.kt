package pachmp.meventer.ui.screens.mainmenu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import pachmp.meventer.ui.screens.mainmenu.bottomScreens.BottomBarScreen

@RootNavGraph
@NavGraph
annotation class MainmenuNavGraph(
    val start: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@MainmenuNavGraph(start = true)
@Destination
@Composable
fun MainmenuScreen() {
    Text("TODO: Bottom navigation with DestinationsNavHost like in MainActivity")
    // TODO: Bottom navigation with DestinationsNavHost like in MainActivity
    MainScreen()
    BackHandler(true) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            BottomNavGraph(navController = navController)
        }

    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    var screens = listOf(
        BottomBarScreen.Profile,
        BottomBarScreen.AllEvents,
        BottomBarScreen.YourEvents
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState() // информация об экране открытом в данный момент
    var currentDestination = navBackStackEntry?.destination


    NavigationBar {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                navDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    navDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = {
            Icon(imageVector = screen.icon, contentDescription = "NavBar Icon")
        },
        label = {
            Text(text = screen.title)
        },
        selected = navDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            if (navController.currentDestination?.hierarchy?.any { it.route == screen.route } != true) {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    )
}
