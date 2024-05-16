package pachmp.meventer.components.mainmenu

import pachmp.meventer.DefaultViewModel
import pachmp.meventer.Navigator
import pachmp.meventer.data.repository.Repositories

abstract class BottomViewModel(
    val rootNavigator: Navigator,
    navigator: Navigator,
    repositories: Repositories
) : DefaultViewModel(navigator, repositories)