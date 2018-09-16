package checker.util.luis.vouchers.database.typeConverters

import android.arch.persistence.room.TypeConverter
import java.util.*

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