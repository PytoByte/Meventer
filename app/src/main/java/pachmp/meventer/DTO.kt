package pachmp.meventer

import kotlinx.serialization.Serializable

@Serializable
data class Response<Type>(
    val result: ResultResponse,
    val data: Type? = null
)

@Serializable
data class ResultResponse(
    val statusCode: Short,
    val message: String
)