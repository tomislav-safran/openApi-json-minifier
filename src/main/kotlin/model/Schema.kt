import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.map

@Serializable
data class Schema(
    @Serializable(with = TypeSerializer::class) val type: List<String?>? = null, // can be a string or a list of strings
    val format: String? = null,
    val items: Schema? = null,
    val properties: Map<String, Schema>? = null,
    @SerialName("\$ref") val ref: String? = null,
) {
    override fun toString(): String {
        ref?.let { return it }
        items?.let { return "type array\n$it" }
        properties?.let {
            val propertiesString = it.entries.joinToString("\n") { (key, value) -> "$key\n$value" }
            return "type object\n$propertiesString"
        }

        return listOfNotNull(
            type?.let { "type $it" },
            format?.let { "format $it" }
        ).joinToString("\n")
    }
}

// Custom serializer for handling both single string and list of strings
object TypeSerializer : KSerializer<List<String?>> {
    override val descriptor: SerialDescriptor = ListSerializer(String.serializer().nullable).descriptor

    override fun serialize(encoder: Encoder, value: List<String?>) {
        if (value.size == 1) {
            // If there's only one element, serialize it as a single string
            encoder.encodeString(value.first() ?: "null")
        } else {
            // Otherwise, serialize it as a list
            encoder.encodeSerializableValue(ListSerializer(String.serializer().nullable), value)
        }
    }

    override fun deserialize(decoder: Decoder): List<String?> {
        val input = decoder as? JsonDecoder ?: throw SerializationException("Expected Json input")
        val element = input.decodeJsonElement()

        return when (element) {
            is JsonArray -> element.map { it.jsonPrimitive.contentOrNull } // Handle array of strings/nulls
            is JsonPrimitive -> listOf(element.content) // Handle single string as list with one element
            else -> throw SerializationException("Expected JsonArray or JsonPrimitive")
        }
    }
}
