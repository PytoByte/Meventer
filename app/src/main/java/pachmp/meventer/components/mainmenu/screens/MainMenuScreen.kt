package pachmp.meventer.components.mainmenu.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import pachmp.meventer.Navigator
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.mainmenu.BottomBarScreen
import pachmp.meventer.components.mainmenu.MainMenuViewModel


@MainmenuNavGraph(start = true)
@Destination
@Composable
fun MainMenuScreen(mainmenuViewModel: MainMenuViewModel = hiltViewModel()) {
    Scaffold(
        bottomBar = {
            BottomBar(navigator = mainmenuViewModel.rootNavigator)
        }
    ) { paddingValues ->
        DestinationsNavHost(
            modifier = Modifier.padding(paddingValues),
            navGraph = NavGraphs.bottom,
            navController = mainmenuViewModel.rootNavigator.navController!!
        )
    }
}

@Composable
fun BottomBar(navigator: Navigator) {
    val currentDestination =
        navigator.navController!!.currentDestinationAsState().value ?: NavGraphs.bottom.startDestination
    NavigationBar {
        BottomBarScreen.values().forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = "NavBar Icon") },
                label = { Text(text = screen.title) },
                selected = currentDestination in screen.navGraph.destinations,
                onClick = {
                    if (currentDestination !in screen.navGraph.destinations) {
                        navigator.clearNavigate(screen.navGraph)
                    }
                }
            )
        }
    }
}

