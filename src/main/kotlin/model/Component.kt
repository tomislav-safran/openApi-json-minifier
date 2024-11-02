package com.tsafran.model

import kotlinx.serialization.Serializable

@Serializable
data class Component(
    val schemas: Map<String, Schema>
) {
    override fun toString(): String {
        return schemas.entries.joinToString("\n") { (key, value) -> "component $key\n$value" }
    }
}
