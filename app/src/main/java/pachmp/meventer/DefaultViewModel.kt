package pachmp.meventer

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.repository.Repositories
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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

    fun cacheFile(uri: Uri, name: String): File {
        val file = File(repositories.appContext.cacheDir, name)
        val instream: InputStream = repositories.appContext.contentResolver.openInputStream(uri)!!
        val output = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var size: Int
        while (instream.read(buffer).also { size = it } != -1) {
            output.write(buffer, 0, size)
        }
        instream.close()
        output.close()

        return file
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