@file:UseSerializers(LocalDateSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.data.Serializers.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class User(
    val id: Int,
    val email: String,
    val avatar: String,
    val dateOfBirth: LocalDate
)