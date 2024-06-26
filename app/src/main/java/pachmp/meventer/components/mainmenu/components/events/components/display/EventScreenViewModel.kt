package pachmp.meventer.components.mainmenu.components.events.components.display

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pachmp.meventer.Nav
import pachmp.meventer.Navigator
import pachmp.meventer.R
import pachmp.meventer.RootNav
import pachmp.meventer.components.destinations.EventScreenDestination
import pachmp.meventer.components.mainmenu.BottomViewModel
import pachmp.meventer.components.mainmenu.components.events.components.display.model.UserModel
import pachmp.meventer.components.widgets.models.FeedbackModel
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventOrganizer
import pachmp.meventer.data.DTO.EventParticipant
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserFeedbackCreate
import pachmp.meventer.data.DTO.UserFeedbackUpdate
import pachmp.meventer.data.enums.Ranks
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

@HiltViewModel
class EventScreenViewModel @Inject constructor(
    @RootNav rootNavigator: Navigator,
    @Nav navigator: Navigator,
    repositories: Repositories,
) : BottomViewModel(rootNavigator, navigator, repositories) {
    var parentSnackbarHostState = snackbarHostState

    var allMembers by mutableStateOf<List<UserModel>?>(null)

    var originatorFeedbacks by mutableStateOf<List<FeedbackModel>?>(null)

    var originatorRating by mutableStateOf(0f)
        private set

    var ready by mutableStateOf<Boolean?>(null)
    var event by mutableStateOf<Event?>(null)
    var appUser by mutableStateOf<UserModel?>(null)
    var originatorUser by mutableStateOf<User?>(null)
    var originator by mutableStateOf<UserModel?>(null)
    var organizers by mutableStateOf<List<UserModel>?>(null)
    var participants by mutableStateOf<List<UserModel>?>(null)

    var rating by mutableStateOf(0f)
    var comment by mutableStateOf("")

    fun init(eventID: Int, appUserID: Int) {
        viewModelScope.launch {
            val eventResponse = repositories.eventRepository.getEvent(eventID)
            if (afterCheckResponse(eventResponse)) {
                event = eventResponse!!.data!!
            } else {
                navigator.clearNavigate(EventScreenDestination)
                parentSnackbarHostState.showSnackbar(repositories.appContext.getString(R.string.user_loading_failed))
                return@launch
            }

            val userResponse = repositories.userRepository.getUserData(appUserID)
            if (afterCheckResponse(userResponse)) {
                appUser = UserModel(
                    id = appUserID,
                    avatar =userResponse!!.data!!.avatar,
                    name = userResponse.data!!.name,
                    ranks = Ranks.getUserRank(event!!, userResponse.data)
                )
            } else {
                navigator.clearNavigate(EventScreenDestination)
                parentSnackbarHostState.showSnackbar(repositories.appContext.getString(R.string.user_loading_failed))
                return@launch
            }

            originator = userModelFromUserID(event!!.originator, Ranks.ORIGINATOR) {
                originatorUser = it
            }

            updateFeedbacks()

            organizers = List(event!!.organizers.size) {
                userModelFromUserID(event!!.organizers[it], Ranks.ORGANIZER)
            }


            participants = List(event!!.participants.size) {
                userModelFromUserID(event!!.participants[it], Ranks.PARTICIPANT)
            }

            allMembers = organizers!! + participants!! + originator!!
            ready = true
        }
    }

    suspend fun updateFeedbacks() {
        var sumRating = 0f
        afterCheckResponse(
            response = repositories.userRepository.getFeedbacks(originator!!.id),
            responseHandler = {
                if (it.value == 404) {
                    originatorFeedbacks = emptyList(); false
                } else null
            }
        ) { response ->
            originatorFeedbacks = List(response.data!!.size) {
                val authorResponse =
                    repositories.userRepository.getUserData(response.data[it].fromUserID)
                sumRating += response.data[it].rating
                if (afterCheckResponse(authorResponse)) {
                    if (authorResponse!!.data!!.id == appUser!!.id) {
                        rating = response.data[it].rating
                        comment = response.data[it].comment
                    }
                    FeedbackModel(
                        id = response.data[it].id,
                        author = authorResponse.data!!,
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
            originatorRating = if (originatorFeedbacks!!.size==0) 0f else sumRating/originatorFeedbacks!!.size
        }
    }

    fun changeUserOrganizer(userModel: UserModel) {
        viewModelScope.launch {
            afterCheckResponse(
                repositories.eventRepository.changeUserOrganizer(
                    EventOrganizer(event!!.id, userModel.id)
                )
            ) {
                val fr = organizers!!.find { userModel.id == it.id }
                if (fr != null) {
                    organizers = organizers!! - userModel
                    participants = participants!! + userModel
                } else {
                    organizers = organizers!! + userModel
                    participants = participants!! - userModel
                }
            }
        }
    }

    fun kickUser(userModel: UserModel) {
        viewModelScope.launch {
            afterCheckResponse(repositories.eventRepository.changeUserParticipant(EventParticipant(userModel.id, event!!.id))) {
                val fr = allMembers!!.find { userModel.id == it.id }!!
                if (userModel.ranks==Ranks.PARTICIPANT) {
                    participants = participants!! - fr
                    allMembers = allMembers!! - fr
                } else if (userModel.ranks==Ranks.ORGANIZER) {
                    organizers = organizers!! - fr
                    allMembers = allMembers!! - fr
                }
            }
        }
    }

    fun changeUserParticipant() {
        viewModelScope.launch {
            afterCheckResponse(repositories.eventRepository.changeUserParticipant(EventParticipant(null, event!!.id))) {
                val fr = allMembers!!.find { appUser!!.id == it.id }
                if (fr != null) {
                    participants = participants!! - fr
                    allMembers = allMembers!! - fr
                } else {
                    participants = participants!! + appUser!!
                    allMembers = allMembers!! + appUser!!
                }
            }
        }
    }

    suspend fun userModelFromUserID(
        userID: Int,
        ranks: Ranks,
        onUserGet: (User) -> Unit = {},
    ): UserModel {
        val userRequest = repositories.userRepository.getUserData(userID)
        if (afterCheckResponse(userRequest)) {
            val user = userRequest!!.data!!
            onUserGet(user)
            return UserModel(
                id = userID,
                avatar = user.avatar,
                name = user.name,
                ranks = ranks
            )
        } else {
            return UserModel(id = userID, avatar = null, name = null, ranks = ranks)
        }

    }

    fun createFeedback() {
        viewModelScope.launch {
            afterCheckResponse(
                repositories.userRepository.createFeedback(
                    UserFeedbackCreate(originatorUser!!.id, rating, comment)
                )
            ) {
                updateFeedbacks()
                parentSnackbarHostState.showSnackbar(repositories.appContext.getString(R.string.feedback_sended))
            }
        }
    }

    fun updateFeedback() {
        viewModelScope.launch {
            val feedback = originatorFeedbacks!!.find { it.author?.id == appUser!!.id }
            if (feedback != null) {
                afterCheckResponse(repositories.userRepository.updateFeedback(
                    UserFeedbackUpdate(feedback.id, rating, comment)
                )) {
                    updateFeedbacks()
                }
            } else {
                parentSnackbarHostState.showSnackbar(repositories.appContext.getString(R.string.feedback_not_found))
            }
        }
    }

    fun deleteFeedback() {
        viewModelScope.launch {
            val feedback = originatorFeedbacks!!.find { it.author?.id == appUser!!.id }
            if (feedback != null) {
                afterCheckResponse(repositories.userRepository.deleteFeedback(feedback.id)) {
                    rating = 0f
                    comment = ""
                    updateFeedbacks()
                }
            } else {
                parentSnackbarHostState.showSnackbar(repositories.appContext.getString(R.string.feedback_not_found))
            }
        }
    }
}


