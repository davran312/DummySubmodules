package namba.wallet.nambaone.common.network.adapter

import com.google.gson.*
import java.lang.reflect.Type
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object DateTimeTypeAdapter : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    private const val pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val dateFormatter = DateTimeFormatter.ofPattern(pattern)

    override fun serialize(
        src: ZonedDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? =
        src?.let { JsonPrimitive(src.format(dateFormatter)) }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime? {
        if (json == null) return null

        return ZonedDateTime.parse(json.asJsonPrimitive.asString)
    }
}
