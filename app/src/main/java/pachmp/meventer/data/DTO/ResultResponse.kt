package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    val code: Short,
    val message: String
)