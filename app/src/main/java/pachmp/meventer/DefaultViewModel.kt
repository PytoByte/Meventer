package pachmp.meventer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
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

        val inputStream =
            repositories.appContext.contentResolver.openInputStream(uri)!! as FileInputStream
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
            when (it.result.value) {
                200 -> {
                    body(it); return true
                }

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

    fun getImage(imageBitmap: MutableState<ImageBitmap>, fileName: String?) {
        if (fileName.isNullOrEmpty().not()) {
            viewModelScope.launch {
                val imageRequested = repositories.fileRepository.getFile(fileName!!)

                if (imageRequested == null) {
                    Log.e("IMAGE", "Сбой в загрузке изображения")
                } else {
                    imageBitmap.value = imageRequested
                }
            }
        }
    }

    fun getImageTest(imageBitmap: MutableState<ImageBitmap>, fileName: String?): MutableState<ImageBitmap> {
        if (fileName.isNullOrEmpty().not()) {
            viewModelScope.launch {
                val imageRequested = repositories.fileRepository.getFile(fileName!!)

                if (imageRequested == null) {
                    Log.e("IMAGE", "Сбой в загрузке изображения")
                } else {
                    imageBitmap.value = imageRequested
                }
            }
        }

        return imageBitmap
    }

    @SuppressLint("Recycle")
    fun getLocalImageUri(imageBitmap: MutableState<ImageBitmap>, fileUri: Uri?) {
        if (fileUri!=null) {
            viewModelScope.launch {
                val inputStream = repositories.appContext.contentResolver.openInputStream(fileUri)!! as FileInputStream
                val bytes = inputStream.readBytes()
                imageBitmap.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                inputStream.close()
            }
        }
    }

    fun getImageUri(imageBitmap: MutableState<ImageBitmap>, fileUri: Uri?) {
        if (fileUri!=null) {
            viewModelScope.launch {
                val imageRequested = repositories.fileRepository.getFileByFullPath(fileUri.toString() ?: "")

                if (imageRequested == null) {
                    Log.e("IMAGE", "Сбой в загрузке изображения")
                } else {
                    imageBitmap.value = imageRequested
                }
            }
        }
    }

    fun getDefaultImageBitmap(): MutableState<ImageBitmap> {
        return mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap())
    }

    fun fixEventImages(event: Event) = event.copy(images = event.images.map { repositories.fileRepository.getFileURL(it) })
    fun fixUserAvatar(user: User) = user.copy(avatar = repositories.fileRepository.getFileURL(user.avatar))
}