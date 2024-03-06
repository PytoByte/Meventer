package pachmp.meventer.repository

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
@Serializable
data class Events(
    val organizers: Array<UInt>,
    val name: String,
    val start: ULong,
    val end: ULong,
    val description: String,
    val picture: String,
    val price: UInt,
    val like: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Events

        if (!organizers.contentEquals(other.organizers)) return false
        if (name != other.name) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (description != other.description) return false
        return picture == other.picture
    }

    override fun hashCode(): Int {
        var result = organizers.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + picture.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + like.hashCode()
        return result
    }
}