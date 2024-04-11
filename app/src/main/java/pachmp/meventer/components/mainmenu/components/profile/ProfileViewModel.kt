package pachmp.meventer.components.mainmenu.components.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.RootNav
import pachmp.meventer.components.NavGraphs
import pachmp.meventer.components.destinations.EmailEditScreenDestination
import pachmp.meventer.components.destinations.PasswordEditScreenDestination
import pachmp.meventer.components.destinations.ProfileEditDestination
import pachmp.meventer.components.destinations.ProfileScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var user by mutableStateOf<User?>(null)
        private set

    var avatar by mutableStateOf<String?>(null)
        private set

    var feedbackModels by mutableStateOf<List<FeedbackModel>?>(null)
        private set

    var avrRating by mutableStateOf(0f)
        private set

    fun updateProfile() {
        feedbackModels = null
        viewModelScope.launch {
            afterCheckResponse(repositories.userRepository.getUserData()) { response ->
                user = fixUserAvatar(response.data!!)
                avatar = user!!.avatar
                println(avatar)
            }

            afterCheckResponse(
                response = repositories.userRepository.getFeedbacks(),
                responseHandler = {
                    if (it.value == 404) {
                        feedbackModels = emptyList(); false
                    } else null
                }
            ) { response ->
                feedbackModels = List(response.data!!.size) {
                    val authorResponse = repositories.userRepository.getUserData(response.data[it].fromUserID)
                    avrRating += response.data[it].rating
                    if (afterCheckResponse(authorResponse)) {
                        FeedbackModel(
                            id = response.data[it].id,
                            author = fixUserAvatar(authorResponse!!.data!!),
                            rating = response.data[it].rating,
                            comment = response.data[it].comment
                        )
                    } else {
                        FeedbackModel(
                            id = response.data[it].id,
                            author = null,
                            rating = response.data[it].rating,
                            comment = response.data[it].comment
                        )
                    }
                }
                avrRating = if (feedbackModels!!.size==0) 0f else avrRating/feedbackModels!!.size
            }
        }
    }

    fun logout() {
        repositories.encryptedSharedPreferences.edit().putString("token", null).apply()
        rootNavigator.clearNavigate(NavGraphs.login)
    }

    fun navigateToEditData() {
        navigator.navigate(ProfileEditDestination)
    }

    fun navigateToEditPassword() {
        navigator.navigate(PasswordEditScreenDestination)
    }

    fun navigateToEditEmail() {
        navigator.navigate(EmailEditScreenDestination)
    }

    fun navigateToProfile() {
        navigator.clearNavigate(ProfileScreenDestination)
    }
}