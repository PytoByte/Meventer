package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class Response<Type>(
    val result: ResultResponse,
    val data: Type? = null
)
