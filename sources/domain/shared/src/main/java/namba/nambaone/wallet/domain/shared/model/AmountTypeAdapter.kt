package namba.nambaone.wallet.domain.shared.model

import com.google.gson.*
import java.lang.reflect.Type
import java.math.BigDecimal

object AmountTypeAdapter : JsonSerializer<Amount>, JsonDeserializer<Amount> {

    override fun serialize(
        src: Amount?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? =
        src?.let {
            JsonPrimitive(it.value.multiply(BigDecimal(100)).toInt().toString())
        }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Amount? =
        json?.let { Amount(it.asJsonPrimitive.asBigDecimal)
            .times(0.01) }

    fun deserialize(
        value: String
    ): Amount = Amount(
        value.toBigDecimal()
    ).times(0.01)
}
