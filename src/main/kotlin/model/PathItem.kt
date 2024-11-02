package com.tsafran.model

import kotlinx.serialization.Serializable

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
