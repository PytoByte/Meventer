package pachmp.meventer.components.mainmenu

import android.content.SharedPreferences
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Navigator
import pachmp.meventer.data.repository.Repositories

open class BottomViewModel(
    val rootNavigator: Navigator,
    navigator: Navigator,
    repositories: Repositories
) : DefaultViewModel(navigator, repositories) {

}