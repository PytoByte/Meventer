@file:UseSerializers(InstantSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import pachmp.meventer.data.Serializers.InstantSerializer
import java.time.Instant

@Serializable
data class MessageSend(
    val chatID: Long,
    val body: String,
    val timestamp: Instant,
    val attachment: String?
)

@Serializable
data class Message(
    @SerialName("messageID")
    val id: Long,
    val chatID: Long,
    val senderID: Int,
    val body: String,
    val timestamp: Instant,
    val attachment: String?
)

@Serializable
data class MessageUpdate(
    @SerialName("messageID")
    val id: Long,
    val chatID: Long,
    val body: String
)

@Serializable
data class MessageUpdated(
    @SerialName("messageID")
    val id: Long,
    val body: String
)

@Serializable
data class MessageDelete(
    @SerialName("messageID")
    val id: Long,
    val chatID: Long
)