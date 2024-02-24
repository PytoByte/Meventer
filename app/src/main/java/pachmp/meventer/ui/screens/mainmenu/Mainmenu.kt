package pachmp.meventer.ui.screens.mainmenu

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
@RootNavGraph
@NavGraph
annotation class MainmenuNavGraph(
    val start: Boolean = false
)

@MainmenuNavGraph(start = true)
@Destination
@Composable
fun MainmenuScreen() {
    Text("TODO: Bottom navigation with DestinationsNavHost like in MainActivity")
    // TODO: Bottom navigation with DestinationsNavHost like in MainActivity
}