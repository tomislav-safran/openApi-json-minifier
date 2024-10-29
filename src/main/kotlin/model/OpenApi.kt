package com.tsafran.model

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
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.toString

@Serializable
data class OpenApi(
    val info: Info,
    val servers: List<Server>,
    val paths: Map<String, PathItem>
) {
    override fun toString(): String {
        val urls = servers.firstOrNull()?.let { "url ${it.url}" }
        val pathsString = paths.entries.joinToString("\n") { (key, value) -> "path $key\n$value" }
        return listOfNotNull(urls, pathsString).joinToString("\n")
    }
}

@Serializable
data class Server(val url: String)

@Serializable
data class Info(
    val title: String,
    val version: String
)

@Serializable
data class PathItem(
    val get: PathMethod? = null,
    val post: PathMethod? = null,
    val put: PathMethod? = null,
    val delete: PathMethod? = null
) {
    override fun toString(): String = listOfNotNull(
        get?.let { "get\n$it" },
        post?.let { "post\n$it" },
        put?.let { "put\n$it" },
        delete?.let { "delete\n$it" }
    ).joinToString("\n")
}

@Serializable
data class PathMethod(
    val operationId: String? = null,
    val parameters: List<Parameter>? = null,
    val responses: Map<String, Response> = mapOf()
) {
    override fun toString(): String {
        val parametersString = parameters?.joinToString("\n") { "params\n$it" }
        val responsesString = "responses\n" + responses.entries.joinToString("\n") { (key, value) -> "$key\n$value" }
        return listOfNotNull("opId $operationId", parametersString, responsesString).joinToString("\n")
    }
}

@Serializable
data class Parameter(
    val name: String,
    @SerialName("in") val location: String,
    val required: Boolean,
    val schema: Schema? = null
) {
    override fun toString(): String {
        return listOf(
            "name $name",
            "in $location",
            "req $required",
            schema?.let { "schema\n$it" }
        ).filterNotNull().joinToString("\n")
    }
}

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

@Serializable
data class Response(
    val content: ContentType? = null,
) {
    override fun toString(): String { return content.toString() }
}

@Serializable
data class ContentType(
    @SerialName("application/json") val applicationJson: ApplicationJson
) {
    override fun toString(): String { return applicationJson.toString() }
}

@Serializable
data class ApplicationJson(
    val schema: Schema
) {
    override fun toString(): String {
        return schema.toString()
    }
}