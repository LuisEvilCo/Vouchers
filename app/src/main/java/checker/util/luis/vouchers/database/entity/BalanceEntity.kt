package checker.util.luis.vouchers.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import checker.util.luis.vouchers.utils.safeString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.util.*


@Entity(tableName = "balance_history")
@JsonAdapter(BalanceEntityDeserializer::class)
class BalanceEntity @JvmOverloads constructor (

    @field:PrimaryKey
    @field:ColumnInfo(name = "uuid")
    var id: UUID = UUID.randomUUID(),

    @field:ColumnInfo(name = "name")
    @SerializedName("nombre")
    var name: String = "",

    @field:ColumnInfo(name = "amount")
    @SerializedName("value")
    var amount: String = "",

    @field:ColumnInfo(name = "lastUpdated")
    var lastUpdated: Date = Calendar.getInstance().time
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BalanceEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (amount != other.amount) return false
        if (lastUpdated != other.lastUpdated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 42 * result + name.hashCode()
        result = 42 * result + amount.hashCode()
        result = 42 * result + lastUpdated.hashCode()
        return result
    }

    fun hasChange(other: Any?): Boolean {
        if (this === other) return false
        if (javaClass != other?.javaClass) return true

        other as BalanceEntity
        if (name != other.name) return true
        if (amount != other.amount) return true

        return false
    }
}

class BalanceEntityDeserializer: JsonDeserializer<BalanceEntity> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BalanceEntity {
        json ?: throw IllegalStateException("JSON does not exist")
        typeOfT ?: throw IllegalStateException("Type does not exist")
        context ?: throw IllegalStateException("Deserialization context does not exist")

        val jsonObject = json.asJsonObject

        val name = jsonObject["nombre"].safeString(context)
        val value = jsonObject["value"].safeString(context)

        return BalanceEntity(
            name = name,
            amount = value
        )
    }
}


class CustomTypeConverters {

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter
    fun toString(value: UUID): String {
        return value.toString()
    }

    @TypeConverter
    fun toUUID(value: String) : UUID {
        return UUID.fromString(value)
    }
}