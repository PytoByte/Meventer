package pachmp.meventer.data.repository

import android.content.SharedPreferences
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import pachmp.meventer.data.DTO.ResultResponse

open class DefaultRepository (val encryptedSharedPreferences: SharedPreferences) {
    private val defaultConfig: HttpClientConfig<CIOEngineConfig>.() -> Unit = {
        install(ContentNegotiation) {
            json()
        }
    }

    protected val baseURL = "http://10.0.2.2:8080/"

    protected suspend fun <Type> withHttpClient(
        config: HttpClientConfig<CIOEngineConfig>.() -> Unit = defaultConfig,
        block: suspend HttpClient.() -> Type
    ): Type? {
        val client = HttpClient(CIO, config)
        val result = with(client) {
            try {
                block()
            } catch (exception: Exception) {
                null
            }
        }
        client.close()
        return result
    }

    suspend fun verifyToken() = withHttpClient {
        get("${baseURL}user/data") {
            url {
                parameters.append("token", getToken())
            }
        }.body<ResultResponse>()
    }

    protected fun getToken() = encryptedSharedPreferences.getString("token", null) ?: throw IllegalStateException("empty token")

    protected suspend fun setTokenHeader(): Headers {
        val token = getToken()
        return headers {
            append(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    fun getFileURL(fileName: String) = "${baseURL}file/${fileName}"
}