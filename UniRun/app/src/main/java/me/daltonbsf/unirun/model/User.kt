package me.daltonbsf.unirun.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class User(
    val uid: String,
    var email: String?,
    var name: String,
    var username: String,
    var phone: String,
    var profileImageURL: String?,
    var bio: String,
    @Serializable(with = LocalDateSerializer::class) // Adicione para LocalDate
    val registrationDate: LocalDate?,
    var offeredRidesCount: Int,
    var requestedRidesCount: Int
)

// Crie um serializador para LocalDate
object LocalDateSerializer : KSerializer<LocalDate?> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: LocalDate?) {
        if (value != null) {
            encoder.encodeString(value.format(formatter))
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): LocalDate? {
        val string = decoder.decodeString()
        return if (string.isNotBlank()) LocalDate.parse(string, formatter) else null
    }
}