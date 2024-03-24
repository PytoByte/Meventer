package pachmp.meventer.data.DTO

import io.ktor.http.HttpStatusCode

data class Response<Type>(
    val result: HttpStatusCode,
    val data: Type? = null
)