package pachmp.meventer

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import pachmp.meventer.data.repository.DatabaseRepository
import pachmp.meventer.data.DTO.ResultResponse
import javax.inject.Inject

open class DefaultViewModel(val navigator: Navigator, val encryptedSharedPreferences: SharedPreferences): ViewModel() {

    val repository = DatabaseRepository()

    val snackbarHostState = SnackbarHostState()

    suspend fun checkResponse(response: ResultResponse?): Boolean {
        if (response==null) {
            snackbarHostState.showSnackbar(message = "Сервер не отвечает", duration = SnackbarDuration.Short)
        } else if (response.code!=200.toShort()) {
            snackbarHostState.showSnackbar(message = response.message, duration = SnackbarDuration.Short)
        } else { return true }
        return false
    }
}