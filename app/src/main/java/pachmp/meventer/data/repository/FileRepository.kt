package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventsGet
import pachmp.meventer.data.DTO.Response
import javax.inject.Inject

class FileRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    private val repositoryURL = baseURL + "file/"

    fun getFileURL(fileName: String) = "${baseURL}file/${fileName}"
    fun getFileName(fileUrl: String) = fileUrl.replace("${baseURL}file/", "")
    fun isServerFile(fileUrl: String) = "${baseURL}file/" in fileUrl

    fun clearCache() {

    }

    // TODO: I think it should require a token
    suspend fun upload(byteArray: ByteArray) = withHttpClient {
        post("${repositoryURL}upload") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(byteArray)
        }.toResponse<String>()
    }
}