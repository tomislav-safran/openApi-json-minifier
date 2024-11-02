package com.tsafran.model

import kotlinx.serialization.Serializable
import kotlin.collections.component1
import kotlin.collections.component2

@Serializable
data class OpenApi(
    val info: Info,
    val servers: List<Server>,
    val paths: Map<String, PathItem>,
    val components: Component
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