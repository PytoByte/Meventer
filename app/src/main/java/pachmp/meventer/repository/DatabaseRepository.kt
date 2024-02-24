package pachmp.meventer.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class DatabaseRepository {

    suspend fun registerRequest(email: String): Boolean {
        /*val client = HttpClient(CIO)
        val response: HttpResponse = client.post("http://127.0.0.1:8080/user/sendEmailCode")
        client.close()*/
        return true
    }

    suspend fun confirmRegister(code: Int): Boolean {
        /*val client = HttpClient(CIO)
        val response: HttpResponse = client.post("http://127.0.0.1:8080/user/verifyEmailCode")
        client.close()*/
        return true
    }

    suspend fun createUser(nickname: String, password: String): Response<String> {
        /*val client = HttpClient(CIO)
        val response: HttpResponse = client.post("http://127.0.0.1:8080/user/register")
        client.close()*/
        return Response(ResultResponse(200, "yes"),data="123")
    }

    suspend fun login(email: String, password: String): Response<String> {
        /*val client = HttpClient(CIO)
        val response: HttpResponse = client.post("https://ktor.io/")
        client.close()*/
        return Response(ResultResponse(200, "yes"),data="123")
    }
}