package com.tsafran.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
