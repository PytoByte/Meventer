package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class NullableUserID(
    val id: Int? = null
)