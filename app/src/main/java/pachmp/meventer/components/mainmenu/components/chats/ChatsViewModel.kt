package pachmp.meventer.components.mainmenu.components.chats

import android.content.SharedPreferences
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.mainmenu.BottomViewModel
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    encryptedSharedPreferences: SharedPreferences,
) : BottomViewModel(rootNavigator, navigator, encryptedSharedPreferences) {

}