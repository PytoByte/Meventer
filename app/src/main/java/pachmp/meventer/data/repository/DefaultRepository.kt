package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


open class DefaultRepository(
    val encryptedSharedPreferences: SharedPreferences,
    val appContext: Context
) {
    private val defaultConfig: HttpClientConfig<CIOEngineConfig>.() -> Unit = {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP call", message)
                }
            }
        }
        /*engine {
            https {
                trustManager = getTrustManagerFactory()?.trustManagers?.first {
                    it is X509TrustManager
                } as X509TrustManager
            }
        }*/
    }

    /*private fun getTrustManagerFactory(): TrustManagerFactory? {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(getKeyStore())
        return trustManagerFactory
    }

    private fun getKeyStore(): KeyStore {

        val tempFile = java.io.File.createTempFile("temp", ".jks")

        val fileOutputStream = FileOutputStream(tempFile)
        val originalInputStream = appContext.assets.use { it.open("keystore.jks") }

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (originalInputStream.read(buffer).also { bytesRead = it } != -1) {
            fileOutputStream.write(buffer, 0, bytesRead)
        }

        originalInputStream.close()
        fileOutputStream.close()

        val fileInputStream = FileInputStream(tempFile)

        //val keyStoreFile = appContext.assets.use { it.open("keystore.jks") }
        //val keyStoreFile = FileInputStream("keystore.jks")
        val keyStorePassword = "password".toCharArray()
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(fileInputStream, keyStorePassword)
        return keyStore
    }*/

    protected val baseURL = "http://10.0.2.2:8080/"

    protected suspend fun <Type> withHttpClient(
        config: HttpClientConfig<CIOEngineConfig>.() -> Unit = defaultConfig,
        block: suspend HttpClient.() -> Type
    ): Type? = HttpClient(CIO, config).use {
        try {
            it.block()
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    protected fun getToken() = encryptedSharedPreferences.getString("token", "")!!

    fun getFileURL(fileName: String) = "${baseURL}file/${fileName}"
}