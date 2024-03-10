@file:UseSerializers(InstantSerializer::class, LocalDateSerializer::class)
package pachmp.meventer.data.DTO

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class Response<Type>(
    val result: ResultResponse?,
    val data: Type? = null
)

@Serializable
data class ResultResponse(
    val code: Short,
    val message: String
)

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
    val participants: List<Int>,
    val originator: Int,
    val organizers: List<Int>,
    val favourite: Boolean
)

@Serializable
data class UserEmailCode(
    val email: String,
    val code: String
)

@Serializable
data class UserLogin(
    val email: String,
    val password: String
)

@Serializable
data class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String? = null,
    val dateOfBirth: String
)

@Serializable
data class User(
    val id: Int,
    val email: String,
    val avatar: String,
    val date: LocalDate
)

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "java.time.Instant", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Instant =
        Instant.parse(decoder.decodeString())
}

const val Date_Pattern = "yyyy-MM-dd"
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor(
        "java.time.LocalDate", PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(
        decoder.decodeString(), DateTimeFormatter.ofPattern(Date_Pattern)
    )

    override fun serialize(encoder: Encoder, value: LocalDate) =
        encoder.encodeString(value.toString())
}