@file:UseSerializers(InstantSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.data.Serializers.InstantSerializer
import java.time.Instant

@Serializable
data class EventCreate(
    val name: String,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?
)
