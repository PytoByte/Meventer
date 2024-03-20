package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FileRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    fun getFileURL(fileName: String) = "${baseURL}file/${fileName}"
    fun getFileName(fileUrl: String) = fileUrl.replace("${baseURL}file/", "")
    fun isServerFile(fileUrl: String) = "${baseURL}file/" in fileUrl
}