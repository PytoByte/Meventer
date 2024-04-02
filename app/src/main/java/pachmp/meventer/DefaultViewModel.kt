package pachmp.meventer

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import io.ktor.http.HttpStatusCode
import pachmp.meventer.components.destinations.LoginScreenDestination
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

open class DefaultViewModel(
    val navigator: Navigator,
    val repositories: Repositories,
) : ViewModel() {

    val snackBarHostState = SnackbarHostState()

    fun cacheFile(uri: Uri, name: String): File {
        val file = File(repositories.appContext.cacheDir, name)
        val inputStream = repositories.appContext.contentResolver
            .openInputStream(uri)!! as FileInputStream
        val output = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var size: Int
        while (inputStream.read(buffer).also { size = it } != -1) {
            output.write(buffer, 0, size)
        }
        inputStream.close()
        output.close()
        return file
    }

    suspend fun <Type> checkResponse(
        response: Response<Type>?, checkToken: Boolean = true,
        requestHandler: (HttpStatusCode) -> Boolean? = { null }
    ): Boolean {
        if (checkToken) {
            val responseToken = repositories.userRepository.verifyToken()
            if (responseToken != null) {
                if (responseToken.result.value == 409) {
                    repositories.encryptedSharedPreferences.edit().clear().apply()
                    navigator.clearNavigate(LoginScreenDestination)
                    return false
                }
            }
        }

        if (response == null) {
            snackBarHostState.showSnackbar(
                message = "Сервер не отвечает",
                duration = SnackbarDuration.Short
            )
            return false
        } else if (response.result.value != 200) {
            if (response.result.value == 409) {
                navigator.clearNavigate(LoginScreenDestination)
                return false
            } else {
                val handlerResult = requestHandler(response.result)
                if (handlerResult != null) {
                    return handlerResult
                } else {
                    snackBarHostState.showSnackbar(
                        message = response.result.description,
                        duration = SnackbarDuration.Short
                    )
                    return false
                }
            }
        }
        return true
    }

    fun fixEventImages(event: Event) =
        event.copy(images = event.images.map { repositories.fileRepository.getFileURL(it) })

    fun fixUserAvatar(user: User) =
        user.copy(avatar = repositories.fileRepository.getFileURL(user.avatar))
}