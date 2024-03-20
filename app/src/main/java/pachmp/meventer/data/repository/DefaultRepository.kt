package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json


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
                trustManager = object: X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) { }

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) { }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                }
            }
        }*/
    }

    /*fun getKeyStore(): KeyStore {
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(appContext.assets.open("keystore.jks"), "XgfX231TufOvGaeTU3Rwvjuf3k6jnvdsesRycToF0BQZ7tkzZ8qsd4yTtc5oNgql".toCharArray())
        return keyStore
    }

    fun getTrustManagerFactory(): TrustManagerFactory? {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(getKeyStore())
        return trustManagerFactory
    }

    fun getSslContext(): SSLContext? {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, getTrustManagerFactory()?.trustManagers, null)
        return sslContext
    }

    fun getTrustManager(): X509TrustManager {
        return getTrustManagerFactory()?.trustManagers?.first { it is X509TrustManager } as X509TrustManager
    }*/

    protected val baseURL = "http://10.0.2.2:8080/"
    //protected val baseURL = "https://127.0.0.1:8080/"

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
}