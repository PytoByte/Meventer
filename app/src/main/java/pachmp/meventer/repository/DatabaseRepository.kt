package pachmp.meventer.repository

<<<<<<< HEAD
=======
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
>>>>>>> VV

class DatabaseRepository {

    suspend fun sendEmailCode(email: String): ResultResponse {
        println(email)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        val response: ResultResponse = client.post("http://10.0.2.2:8080/user/sendEmailCode") {
            setBody(email)
        }.body()
        client.close()
        return response
    }

    suspend fun verifyEmailCode(userEmailCode: UserEmailCode): ResultResponse {
        println(userEmailCode)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        val response: ResultResponse = client.post("http://10.0.2.2:8080/user/verifyEmailCode") {
            contentType(ContentType.Application.Json)
            setBody(userEmailCode)
        }.body()
        client.close()
        return response
    }

    suspend fun register(userRegister: UserRegister): Response<String?> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: Response<String?> = client.post("http://10.0.2.2:8080/user/register"){
            contentType(ContentType.Application.Json)
            setBody(userRegister)
        }.body()
        client.close()
        return response
    }

    suspend fun login(userLogin: UserLogin): Response<String?> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        val res: String = client.post("http://10.0.2.2:8080/user/login"){
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }.body()
        println(res)

        val response: Response<String?> = client.post("http://10.0.2.2:8080/user/login"){
            contentType(ContentType.Application.Json)
            setBody(userLogin)
        }.body()
        client.close()
        return response
    }
}