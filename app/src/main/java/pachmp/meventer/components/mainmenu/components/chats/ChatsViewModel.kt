package pachmp.meventer.components.mainmenu.components.chats

import android.content.SharedPreferences
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.components.mainmenu.MainMenuViewModel
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories)  {

}