package com.tsafran.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val responses: Map<String, Response>
) {
    override fun toString(): String {
        val parametersString = parameters?.joinToString("\n") { "params\n$it" }
        return listOfNotNull("opId $operationId", parametersString).joinToString("\n")
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
    val type: String? = null,
    val format: String? = null
) {
    override fun toString(): String = listOfNotNull(
        type?.let { "type $it" },
        format?.let { "format $it" }
    ).joinToString("\n")
}

@Serializable
data class Response(val description: String? = null)