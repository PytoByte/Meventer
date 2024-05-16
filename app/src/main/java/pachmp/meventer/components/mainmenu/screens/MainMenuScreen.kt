package pachmp.meventer.components.mainmenu.screens

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import pachmp.meventer.Navigator
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.mainmenu.MainMenuViewModel
import pachmp.meventer.components.mainmenu.components.chats.ChatsViewModel
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.data.enums.BottomBarScreens


@MainmenuNavGraph(start = true)
@Destination
@Composable
fun MainMenuScreen(startRoute: String="Events", mainmenuViewModel: MainMenuViewModel = hiltViewModel()) {
    mainmenuViewModel.navigator.setController(rememberNavController())

    var bottomBarShow by remember { mutableStateOf(true) }

    val eventsViewModel: EventsViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val chatsViewModel: ChatsViewModel = hiltViewModel()


    mainmenuViewModel.navigator.getController()!!.addOnDestinationChangedListener { controller, destination, arguments ->
        var start = false
        BottomBarScreens.values().forEach {
            if (it.navGraph.startDestination.route == destination.route) {
                start = true
            }
        }

        if (BottomBarScreens.Profile.navGraph.startDestination.route == destination.route) {
            Log.w("NAVIGATOR", "detected ProfileScreen")
            profileViewModel.updateProfile()
        }

        else if (BottomBarScreens.Events.navGraph.startDestination.route == destination.route) {
            Log.w("NAVIGATOR", "detected AllEventsScreen")
            eventsViewModel.updateEvents()
        }

        else if (BottomBarScreens.Chats.navGraph.startDestination.route == destination.route) {
            Log.w("NAVIGATOR", "detected ChatsScreen")
            chatsViewModel.initSocket()
            chatsViewModel.updateChats()
        }

        bottomBarShow = start
    }

    val animateBottomBar by animateDpAsState(
        targetValue = if (bottomBarShow) 100.dp else 0.dp,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing), label = ""
    )

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max=animateBottomBar)) {
                BottomBar(navigator = mainmenuViewModel.navigator)
            }
        }
    ) { paddingValues ->
        DestinationsNavHost(
            modifier = Modifier.padding(paddingValues),
            navGraph = NavGraphs.bottom,
            navController = mainmenuViewModel.navigator.getController()!!,
            startRoute = BottomBarScreens.valueOf(startRoute).navGraph,
            dependenciesContainerBuilder = {
                dependency(NavGraphs.events) {
                    eventsViewModel
                }

                dependency(NavGraphs.chats) {
                    chatsViewModel
                }

                dependency(NavGraphs.profile) {
                    profileViewModel
                }
            }
        )
    }
}

@Composable
fun BottomBar(navigator: Navigator) {
    val currentDestination =
        navigator.getController()!!.currentDestinationAsState().value
            ?: NavGraphs.bottom.startDestination
    NavigationBar {
        BottomBarScreens.values().forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = "NavBar Icon") },
                label = { Text(text = stringResource(screen.titleResourseID)) },
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

