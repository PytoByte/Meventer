package pachmp.meventer.ui.screens.mainmenu

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pachmp.meventer.ui.screens.mainmenu.bottomScreens.AllEventsScreen
import pachmp.meventer.ui.screens.mainmenu.bottomScreens.BottomBarScreen
import pachmp.meventer.ui.screens.mainmenu.bottomScreens.ProfileScreen
import pachmp.meventer.ui.screens.mainmenu.bottomScreens.YourEventsScreen

@Composable
fun BottomNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.AllEvents.route
    ) {
        composable(route = BottomBarScreen.AllEvents.route) {
            AllEventsScreen()
        }
        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen()
        }
        composable(route = BottomBarScreen.YourEvents.route) {
            YourEventsScreen()
        }
    }

}