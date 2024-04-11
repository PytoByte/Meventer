package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.fullPath
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pachmp.meventer.R
import pachmp.meventer.data.DTO.Response
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


open class DefaultRepository(
    val encryptedSharedPreferences: SharedPreferences,
    val appContext: Context
) {
    private val defaultConfig: HttpClientConfig<OkHttpConfig>.() -> Unit = {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor response", message)
                }
            }
        }

        engine {
            config {
                pingInterval(20, TimeUnit.SECONDS)
                hostnameVerifier { _, _ -> true }
                val cf = CertificateFactory.getInstance("X.509")
                val cert = appContext.resources.openRawResource(R.raw.my_key_store)
                try {
                    val ca = cf.generateCertificate(cert)

                    val keyStoreType = KeyStore.getDefaultType()
                    val keyStore = KeyStore.getInstance(keyStoreType)
                    keyStore.load(null, null)
                    keyStore.setCertificateEntry("ca", ca)

                    val tmfAlgo = TrustManagerFactory.getDefaultAlgorithm()
                    val tmf = TrustManagerFactory.getInstance(tmfAlgo)
                    tmf.init(keyStore)

                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, tmf.trustManagers, null)

                    sslSocketFactory(
                        sslContext.socketFactory,
                        tmf.trustManagers[0] as X509TrustManager
                    )
                } finally {
                    cert.close()
                }

            }
        }
    }

    //protected val baseURL = "https://127.0.0.1:8080/"
    private val serverIP = "89.23.99.58"
    protected val baseURL = "https://${serverIP}:80/"

    protected suspend fun <Type> withHttpClient(
        config: HttpClientConfig<OkHttpConfig>.() -> Unit = defaultConfig,
        block: suspend HttpClient.() -> Type
    ): Type? = HttpClient(OkHttp, config).use {
        try {
            it.block()
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    protected fun getToken() = encryptedSharedPreferences.getString("token", "")!!

    suspend inline fun <reified T>HttpResponse.toResponse(): Response<T> {
        Log.d("http to response", "CODE: ${this.status.value}\nFROM: ${this.request.url.fullPath}\nMESSAGE: ${this.status.description}\nBODY: ${this.body<String>()}\nEND")
        return Response(this.status, this.body<T>())
    }
}