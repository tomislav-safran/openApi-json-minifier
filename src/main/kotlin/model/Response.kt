import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
