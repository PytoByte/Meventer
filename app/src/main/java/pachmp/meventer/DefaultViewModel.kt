package pachmp.meventer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import pachmp.meventer.components.destinations.LoginScreenDestination
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.repository.Repositories
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


abstract class DefaultViewModel(
    val navigator: Navigator,
    val repositories: Repositories,
) : ViewModel() {

    val snackBarHostState = SnackbarHostState()

    fun getFileName(filePath: String): String {
        return filePath.substringAfterLast("/")
    }

    fun getMimeType(uri: Uri): String? {
        return repositories.appContext.contentResolver.getType(uri)
    }

    fun getFileExtension(uri: Uri): String {
        /*val fileType = getMimeType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType) ?: "file"*/
        return getUriFileName(uri).substringAfterLast('.')
    }

    fun getFileExtension(path: String): String {
        return path.substringAfterLast('.')
    }

    fun cacheFile(uri: Uri, name: String): File? {
        val extension = getFileExtension(uri)

        val file = File(repositories.appContext.cacheDir, "${name}.${extension}")

        repositories.appContext.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(1024)
                var size: Int
                while (input.read(buffer).also { size = it } != -1) {
                    output.write(buffer, 0, size)
                }
            }
        } ?: return null

        return file
    }

    private fun cacheImage(imageBitmap: ImageBitmap, name: String): File {
        val newName = name.split(".")[0]+".jpg"
        val file = File(repositories.appContext.cacheDir, newName)

        FileOutputStream(file).use { output ->
            imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 60, output)
        }

        Log.d("IMAGE CACHE", "Caching $newName")

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
                        message = "(${response.result.value}) ${response.result.description}",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } ?: yield()

        return false
    }

    private fun isServerFile(path: String): Boolean {
        return repositories.fileRepository.isServerFile(path)
    }

    @Composable
    fun getImageFromName(fileName: String?): MutableState<ImageBitmap> = remember {
        val imageBitmap = getDefaultImageBitmap()

        if (fileName.isNullOrEmpty().not()) {
            viewModelScope.launch {

                val cacheImage = checkCachedImage(fileName!!)
                if (cacheImage != null) {
                    imageBitmap.value = cacheImage
                    return@launch
                }

                val imageRequested = repositories.fileRepository.getFile(fileName)

                if (imageRequested == null) {
                    Log.e("IMAGE", "Сбой в загрузке изображения")
                } else {
                    cacheImage(imageRequested, fileName)
                    imageBitmap.value = imageRequested
                }
            }
        }

        return@remember imageBitmap
    }

    @Composable
    fun getImageFromUri(uri: Uri?): MutableState<ImageBitmap> = remember {
        val imageBitmap = getDefaultImageBitmap()

        if (uri != null) {
            viewModelScope.launch {
                val cacheImage = checkCachedImage(getUriFileName(uri))
                if (cacheImage != null) {
                    imageBitmap.value = cacheImage
                    return@launch
                }

                if (isServerFile(uri.toString())) {
                    getGlobalImageUri(imageBitmap, uri)
                } else {
                    getLocalImageUri(imageBitmap, uri)
                }
            }
        }

        return@remember imageBitmap
    }

    private fun checkCachedImage(fileName: String): ImageBitmap? {
        val name = "${fileName.split(".")[0]}.jpg"
        repositories.appContext.cacheDir.listFiles()?.forEach {
            if (it.name == name) {
                val inputStream = it.inputStream()

                return inputStream.use {
                    val bytes = inputStream.readBytes()
                    Log.d("IMAGE CACHE", "Found $name")
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                }
            }
        }
        return null
    }

    private fun getGlobalImageUri(imageBitmap: MutableState<ImageBitmap>, fileUri: Uri) {
        viewModelScope.launch {
            val imageRequested =
                repositories.fileRepository.getFileByFullPath(fileUri.toString())

            if (imageRequested == null) {
                Log.e("IMAGE", "Сбой в загрузке изображения")
            } else {
                cacheImage(imageRequested, getUriFileName(fileUri))
                imageBitmap.value = imageRequested
            }
        }
    }

    private fun getLocalImageUri(imageBitmap: MutableState<ImageBitmap>, fileUri: Uri) {
        viewModelScope.launch {
            val inputStream =
                repositories.appContext.contentResolver.openInputStream(fileUri)!! as FileInputStream
            inputStream.use {
                val bytes = inputStream.readBytes()
                imageBitmap.value =
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            }
        }
    }

    fun getDefaultImageBitmap(): MutableState<ImageBitmap> {
        return mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap())
    }

    fun fixEventImages(event: Event) =
        event.copy(images = event.images.map { repositories.fileRepository.getFileURL(it) })

    fun fixUserAvatar(user: User) =
        user.copy(avatar = repositories.fileRepository.getFileURL(user.avatar))

    fun download(fileName: String) {
        val url = repositories.fileRepository.getFileURL(fileName)
        val mimeType = getMimeType(url.toUri())?:"*/*"
        viewModelScope.launch {
            repositories.fileRepository.download(url.toUri(), fileName, mimeType)
        }
    }

    fun customDownload(dir:Uri, fileName: String) = viewModelScope.launch {
        repositories.fileRepository.downloadCustom(dir, fileName)
    }

        /*viewModelScope.launch {
        repositories.fileRepository.downloadCustom(dir, fileName)
    }*/

    fun getUriFileName(uri: Uri): String {
        val returnCursor =
            repositories.appContext.contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val fileName = returnCursor.getString(nameIndex)
            returnCursor.close()
            return fileName
        }
        return ""
    }
}