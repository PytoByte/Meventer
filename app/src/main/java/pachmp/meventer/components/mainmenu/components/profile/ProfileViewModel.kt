package pachmp.meventer.components.mainmenu.components.profile

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.User
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    encryptedSharedPreferences: SharedPreferences,
) : BottomViewModel(rootNavigator, navigator, encryptedSharedPreferences) {
    var user by mutableStateOf<User?>(null)
        private set


    init {
        viewModelScope.launch {
            user = repository.getUserByToken(encryptedSharedPreferences.getString("token", "")!!)
        }
    }

    fun logout() {
        encryptedSharedPreferences.edit().clear().apply()
        rootNavigator.clearNavigate(NavGraphs.login)
    }
}