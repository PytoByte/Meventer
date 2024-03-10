package pachmp.meventer.components.mainmenu

import android.content.SharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    encryptedSharedPreferences: SharedPreferences,
) : BottomViewModel(rootNavigator, navigator, encryptedSharedPreferences) {

    init {
        println(navigator)
        println(rootNavigator)
    }

}