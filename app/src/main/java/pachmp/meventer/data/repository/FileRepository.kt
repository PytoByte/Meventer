package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.toByteArray
import javax.inject.Inject


class FileRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context,
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    private val repositoryURL = baseURL + "file/"

    suspend fun getFile(fileName: String) = withHttpClient {
        val bitmapdata = get("${repositoryURL}${fileName}").bodyAsChannel().toByteArray()
        BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size).asImageBitmap()
    }

    suspend fun getFileByFullPath(filePath: String) = withHttpClient {
        val bitmapdata = get(filePath).bodyAsChannel().toByteArray()
        BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size).asImageBitmap()
    }

    fun getFileURL(fileName: String) = "${repositoryURL}${fileName}"
    fun getFileName(fileUrl: String) = fileUrl.replace(repositoryURL, "")
    fun isServerFile(fileUrl: String) = fileUrl.contains(baseURL)

    fun clearCache() {

    }

    suspend fun upload(byteArray: ByteArray) = withHttpClient {
        post("${repositoryURL}upload") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(byteArray)
        }.toResponse<String>()
    }
}