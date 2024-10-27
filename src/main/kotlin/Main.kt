package com.tsafran

import com.tsafran.model.OpenApi
import kotlinx.serialization.json.Json

fun main() {
    val openApi = loadOpenApiFile("file-name.json")
    println(openApi)
}

fun loadOpenApiFile(fileName: String): OpenApi {
    val inputStream = object {}.javaClass.getResourceAsStream("/$fileName")
        ?: throw IllegalArgumentException("File not found: $fileName")

    val jsonString = inputStream.bufferedReader().use { it.readText() }
    val json = Json { ignoreUnknownKeys = true }

    return json.decodeFromString(jsonString)
}