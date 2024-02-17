package pachmp.meventer

class Database {
    fun registerRequest(email: String, password: String): Boolean {
        return true
    }

    fun confirmRegister(code: Int): Boolean {
        return true
    }

    fun createUser(nickname: String): Response<String> {
        return Response(ResultResponse(200, "yes"),data="123")
    }

    fun login(email: String, password: String): Response<String> {
        return Response(ResultResponse(200, "yes"),data="123")
    }
}