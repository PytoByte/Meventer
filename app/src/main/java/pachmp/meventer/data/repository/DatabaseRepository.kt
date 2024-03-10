package pachmp.meventer.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.DTO.User
import pachmp.meventer.data.DTO.UserEmailCode
import pachmp.meventer.data.DTO.UserLogin
import pachmp.meventer.data.DTO.UserRegister
import java.time.Instant
import java.time.LocalDate

class DatabaseRepository {
    private val defaultConfig: HttpClientConfig<CIOEngineConfig>.() -> Unit = {
        install(ContentNegotiation) {
            json()
        }
    }

    private val defaultResponse = Response<String?>(null, null)

    private suspend fun <Type> withHttpClient(
        default: Type? = null,
        config: HttpClientConfig<CIOEngineConfig>.() -> Unit = defaultConfig,
        block: suspend HttpClient.() -> Type
    ): Type? {
        val client = HttpClient(CIO, config)
        val result = with(client) {
            try {
                block()
            } catch (exception: Exception) {
                default
            }
        }
        client.close()
        return result ?: default
    }

    suspend fun sendEmailCode(email: String) = withHttpClient {
        post("http://10.0.2.2:8080/user/sendEmailCode") { setBody(email) }
            .body<ResultResponse>()
    }

    suspend fun verifyEmailCode(userEmailCode: UserEmailCode) = withHttpClient {
        post("http://10.0.2.2:8080/user/verifyEmailCode") {
            contentType(ContentType.Application.Json)
            setBody(userEmailCode)
        }
            .body<ResultResponse>()
    }

    suspend fun register(userRegister: UserRegister) = withHttpClient(defaultResponse) {
        post("http://10.0.2.2:8080/user/register") {
            contentType(ContentType.Application.Json)
            setBody(userRegister)
        }
            .body()
    }!!

    suspend fun login(userLogin: UserLogin) = withHttpClient(defaultResponse) {
        post("http://10.0.2.2:8080/user/login") {
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }
            .body()
    }!!

    /*suspend fun getUserEvents() = withHttpClient(defaultResponse) {
        post("http://127.0.0.1:8080/event/user") {
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }
            .body()
    }!!*/

    suspend fun getUserEvents() = arrayListOf<Event>().apply {
        add(
            Event(
                id = 0,
                name = "Битва хоров в лицее",
                images = listOf(
                    "https://cdn-icons-png.flaticon.com/128/149/149452.png",
                    "https://img.freepik.com/free-photo/cute-domestic-kitten-sits-at-window-staring-outside-generative-ai_188544-12519.jpg?w=900&t=st=1709883080~exp=1709883680~hmac=eae622ca729fe9cf9b23e2cda6049982c59139965da85c7da403a1d8f669c568"
                ),
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                startTime = Instant.now(),
                minimalAge = 0.toShort(),
                maximalAge = null,
                price = 0,
                participants = listOf(0),
                organizers = listOf(0),
                originator = 0,
                favourite = true
            )
        )
        add(
            Event(
                id = 1,
                name = "Битва хоров в лицее",
                images = listOf("https://cdn-icons-png.flaticon.com/128/149/149452.png"),
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                startTime = Instant.now(),
                minimalAge = 13.toShort(),
                maximalAge = 18.toShort(),
                price = 0,
                participants = listOf(0),
                organizers = listOf(0),
                originator = 0,
                favourite = false
            )
        )
        add(
            Event(
                id = 2,
                name = "Битва хоров в лицее",
                images = listOf("https://cdn-icons-png.flaticon.com/128/149/149452.png"),
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                startTime = Instant.now(),
                minimalAge = 20.toShort(),
                maximalAge = null,
                price = 0,
                participants = listOf(0),
                organizers = listOf(0),
                originator = 0,
                favourite = true
            )
        )
        add(
            Event(
                id = 3,
                name = "Битва хоров в лицее",
                images = listOf("https://cdn-icons-png.flaticon.com/128/149/149452.png"),
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                startTime = Instant.now(),
                minimalAge = 30.toShort(),
                maximalAge = null,
                price = 1000,
                participants = listOf(0),
                organizers = listOf(0),
                originator = 0,
                favourite = false
            )
        )
    }

    suspend fun getEventByID(eventID: Int): Event? {
        return getUserEvents().find { it.id==eventID }
    }

    suspend fun getUserByToken(token: String): User {
        return User(id = 0, email = "...@.com", avatar = "https://static-00.iconduck.com/assets.00/avatar-default-symbolic-icon-479x512-n8sg74wg.png", date = LocalDate.now())
    }

    suspend fun changeFavourite(eventID: Int) {

    }
}