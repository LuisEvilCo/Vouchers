package checker.util.luis.vouchers.model

import checker.util.luis.vouchers.utils.safeString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.reflect.Type

@JsonAdapter(BalanceDeserializer::class)
data class Balance(
    @SerializedName("nombre")
    val name : String,
    @SerializedName("value")
    val amount : String
) : Serializable


class BalanceDeserializer : JsonDeserializer<Balance> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Balance {
        json ?: throw IllegalStateException("JSON does not exist")
        typeOfT ?: throw IllegalStateException("Type does not exist")
        context ?: throw IllegalStateException("Deserialization context does not exist")

        val jSon = json.asJsonObject

        val name = jSon.get("nombre").safeString(context)
        val value = jSon.get("value").safeString(context)

        return Balance(
            name = name,
            amount = value
        )
    }
}