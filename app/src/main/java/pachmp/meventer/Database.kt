package pachmp.meventer

class Database {
    suspend fun registerRequest(email: String, password: String): Boolean {
        return true
    }

    suspend fun confirmRegister(code: Int): Boolean {
        return true
    }

    suspend fun createUser(nickname: String): Response<String> {
        return Response(ResultResponse(200, "yes"),data="123")
    }

    suspend fun login(email: String, password: String): Response<String> {
        return Response(ResultResponse(200, "yes"),data="123")
    }
}