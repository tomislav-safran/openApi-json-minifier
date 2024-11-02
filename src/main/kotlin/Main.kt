package com.tsafran

import com.tsafran.model.OpenApi
import kotlinx.serialization.json.Json

fun main() {
    val openApi = loadOpenApiFile("winx-api.json")
    val minifiedWithResolvedReferences = resolveReferences(openApi, openApi.toString())

    println(minifiedWithResolvedReferences)
}

fun loadOpenApiFile(fileName: String): OpenApi {
    val inputStream = object {}.javaClass.getResourceAsStream("/$fileName")
        ?: throw IllegalArgumentException("File not found: $fileName")

    val jsonString = inputStream.bufferedReader().use { it.readText() }
    val json = Json { ignoreUnknownKeys = true }

    return json.decodeFromString(jsonString)
}

fun resolveReferences(openApi: OpenApi, minified: String): String {
    var resolved = minified
    openApi.components.schemas.forEach { (key, schema) ->
        resolved = resolved.replace("#/components/schemas/$key", schema.toString())
    }
    return resolved
}