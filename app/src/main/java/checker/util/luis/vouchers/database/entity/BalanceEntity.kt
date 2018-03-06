package checker.util.luis.vouchers.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import java.util.*


@Entity(tableName = "balance_history")
class BalanceEntity @JvmOverloads constructor (

    @field:PrimaryKey
    @field:ColumnInfo(name = "uuid")
    var id: UUID = UUID.randomUUID(),

    @field:ColumnInfo(name = "name")
    var name: String = "",

    @field:ColumnInfo(name = "amount")
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