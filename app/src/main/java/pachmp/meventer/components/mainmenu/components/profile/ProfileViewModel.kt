package pachmp.meventer.components.mainmenu.components.profile

import android.content.SharedPreferences
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
import pachmp.meventer.data.DTO.NullableUserID
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserFeedback
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories
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
        viewModelScope.launch {
            val response = repositories.userRepository.getUserData()
            if (checkResponse(response)) {
                user = fixUserAvatar(response!!.data!!)
                avatar = user!!.avatar
            }

            val feedbacksResponse = repositories.userRepository.getFeedbacks()
            if (checkResponse(feedbacksResponse) {
                if (it.value==404) { feedbackModels = emptyList(); false } else {  null } }) {
                feedbackModels = List(feedbacksResponse!!.data!!.size) {
                    val authorResponse = repositories.userRepository.getUserData(feedbacksResponse.data!![it].fromUserID)
                    avrRating += feedbacksResponse.data[it].rating
                    if (checkResponse(authorResponse)) {
                        FeedbackModel(
                            id = feedbacksResponse.data[it].id,
                            author = fixUserAvatar(authorResponse!!.data!!),
                            rating = feedbacksResponse.data[it].rating,
                            comment = feedbacksResponse.data[it].comment
                        )
                    } else {
                        FeedbackModel(
                            id = feedbacksResponse.data[it].id,
                            author = null,
                            rating = feedbacksResponse.data[it].rating,
                            comment = feedbacksResponse.data[it].comment
                        )
                    }
                }
                avrRating /= feedbackModels!!.size
            }
        }
    }

    fun logout() {
        repositories.encryptedSharedPreferences.edit().putString("token", null).apply()
        rootNavigator.clearNavigate(NavGraphs.login)
    }

    fun navigateToEditData() {
        navigator.clearNavigate(ProfileEditDestination)
    }
    fun navigateToEditPassword() {
        navigator.clearNavigate(PasswordEditScreenDestination)
    }
    fun navigateToEditEmail() {
        navigator.clearNavigate(EmailEditScreenDestination)
    }

    fun navigateToProfile() {
        navigator.clearNavigate(ProfileScreenDestination)
    }
}