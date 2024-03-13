package pachmp.meventer.data.Serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object LocalDateSerializer : KSerializer<LocalDate> {
    val Date_Pattern = "yyyy-MM-dd"

    override val descriptor = PrimitiveSerialDescriptor(
        "java.time.LocalDate", PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(
        decoder.decodeString(), DateTimeFormatter.ofPattern(Date_Pattern)
    )

    override fun serialize(encoder: Encoder, value: LocalDate) =
        encoder.encodeString(value.toString())
}