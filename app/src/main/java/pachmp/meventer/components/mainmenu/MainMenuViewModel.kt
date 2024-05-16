package pachmp.meventer.components.mainmenu

import dagger.hilt.android.lifecycle.HiltViewModel
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories
) : BottomViewModel(rootNavigator, navigator, repositories) {

}
