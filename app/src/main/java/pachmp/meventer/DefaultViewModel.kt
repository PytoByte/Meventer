package pachmp.meventer

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
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

    @SuppressLint("Recycle")
    fun cacheFile(uri: Uri, name: String): File {

        val fileType = repositories.appContext.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)

        val inputStream = repositories.appContext.contentResolver.openInputStream(uri)!! as FileInputStream
        val file = File(repositories.appContext.cacheDir, "${name}.${extension}")
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

    suspend fun <Type> afterCheckResponse(
        response: Response<Type>?,
        responseHandler: (HttpStatusCode) -> Boolean? = { null },
        body: suspend (Response<Type>) -> Unit = {},
    ): Boolean {
        response?.let {
            when(it.result.value){
                200 -> {body(it); return true}
                409, 401 -> navigator.clearNavigate(LoginScreenDestination)
                else -> {
                    responseHandler(response.result)?.let { handlerResult ->
                        if (handlerResult) {
                            body(it)
                        }
                        return handlerResult
                    } ?: snackBarHostState.showSnackbar(
                        message = response.result.description,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } ?: snackBarHostState.showSnackbar(
            message = "Сервер не отвечает",
            duration = SnackbarDuration.Short
        )
        return false
    }

    fun fixEventImages(event: Event) = event.copy(images = event.images.map { repositories.fileRepository.getFileURL(it) })
    fun fixUserAvatar(user: User) = user.copy(avatar = repositories.fileRepository.getFileURL(user.avatar))
}