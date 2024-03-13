package pachmp.meventer

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.repository.Repositories
import javax.inject.Inject

open class DefaultViewModel(
    val navigator: Navigator,
    val repositories: Repositories
): ViewModel() {

    val snackbarHostState = SnackbarHostState()

    suspend fun checkResultResponse(response: ResultResponse?): Boolean {
        if (response==null) {
            snackbarHostState.showSnackbar(message = "Сервер не отвечает", duration = SnackbarDuration.Short)
        } else if (response.code!=200.toShort()) {
            snackbarHostState.showSnackbar(message = response.message, duration = SnackbarDuration.Short)
        } else { return true }
        return false
    }

    suspend fun <Type>checkResponse(response: Response<Type>?): Boolean {
        if (response==null) {
            snackbarHostState.showSnackbar(message = "Сервер не отвечает", duration = SnackbarDuration.Short)
        } else if (response.result.code!=200.toShort()) {
            snackbarHostState.showSnackbar(message = response.result.message, duration = SnackbarDuration.Short)
        } else { return true }
        return false
    }
}