package pachmp.meventer.data.repository

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pachmp.meventer.R
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


class FileRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context,
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    private val repositoryURL = baseURL + "file/"

    suspend fun getFile(fileName: String) = withHttpClient {
        val bitmapData = get("${repositoryURL}${fileName}").bodyAsChannel().toByteArray()

        val outputByte = ByteArrayOutputStream()

        outputByte.use { output ->
            BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.size).compress(Bitmap.CompressFormat.JPEG, 60, output)
        }

        val byteArray = outputByte.toByteArray()

        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()
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

    suspend fun upload(file: File) = withHttpClient {
        post("${repositoryURL}upload") {
            bearerAuth(getToken())
            contentType(ContentType("application", file.extension.split(" ")[0]))
            setBody(file.readBytes())
        }.toResponse<String>()
    }

    fun download(uri: Uri, fileName: String, mimeType: String)  {
        val downloadManager = appContext.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(uri)
            .setMimeType(mimeType)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .addRequestHeader(HttpHeaders.Authorization, getToken())
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        downloadManager.enqueue(request)
    }

    suspend fun downloadCustom(dir: Uri, fileName: String) = withHttpClient {
        val notificationID = (1..100).random()

        val mNotificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("MeventerChannel",
                "Meventer",
                NotificationManager.IMPORTANCE_LOW). apply {
                setSound(null, null)
                description = "Meventer notification channel"
            }
            mNotificationManager.createNotificationChannel(channel)
        }

        val builderDownloading = NotificationCompat.Builder(appContext, "MeventerChannel")
            .setContentTitle("Загрузка файла")
            .setContentText(fileName)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true)

        with(NotificationManagerCompat.from(appContext)) {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationID, builderDownloading.build())
            }
        }

        withContext(Dispatchers.IO) {
            appContext.contentResolver.openOutputStream(dir).use { output ->
                output!!.write(get("${repositoryURL}${fileName}").bodyAsChannel().toByteArray())
            }
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            data = dir
        }

        val pendingIntent = PendingIntent.getActivity(appContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builderOpen = NotificationCompat.Builder(appContext, "MeventerChannel")
            .setContentTitle("Загрузка файла завершена")
            .setContentText(fileName)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true).setContentIntent(pendingIntent)

        println("Вроде скачал")
        with(NotificationManagerCompat.from(appContext)) {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationID, builderOpen.build())
            }
        }
    }
}