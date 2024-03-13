@file:UseSerializers(InstantSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.data.Serializers.InstantSerializer
import java.time.Instant

@Serializable
data class Event(
    val id: Int,
    val name: String,
    val images: List<String>,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short,
    val maximalAge: Short?,
    val price: Int,
    val originator: Int,
    val organizers: List<Int>
)