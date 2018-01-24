package checker.util.luis.vouchers.utils

import android.os.Build
import android.support.annotation.RequiresApi
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

val gson = GsonBuilder()
    .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
    .create()


class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
    @RequiresApi(Build.VERSION_CODES.O) // TODO , better handling of this thing
    override fun serialize(
        p0: LocalDateTime?,
        p1: Type?,
        p2: JsonSerializationContext?
    ): JsonElement {
        val zoneId = ZoneId.systemDefault()

        val epoch = p0?.atZone(zoneId)?.toEpochSecond()

        return JsonPrimitive(epoch)
    }
}

class LocalDateSerializer : JsonSerializer<LocalDate> {
    @RequiresApi(Build.VERSION_CODES.O) // TODO , better handling of this thing
    override fun serialize(p0: LocalDate?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val zoneId = ZoneId.systemDefault()

        val epoch = p0?.atStartOfDay(zoneId)?.toEpochSecond()

        return JsonPrimitive(epoch)
    }
}

fun JsonElement?.safeString(context: JsonDeserializationContext, default: String = ""): String {
    return this.deserializeSafe(context, { default }) {
        it.isJsonPrimitive
    }
}

inline fun <reified T : Any> JsonElement?.deserializeSafe(
    context: JsonDeserializationContext,
    provider: (JsonElement?) -> T, predicate: (JsonElement) -> Boolean?
): T {
    this ?: return provider(this)

    return if (predicate(this) ?: false) { // TODO replace with equality check ?
        context.deserialize(this, T::class.java)
    } else {
        provider(this)
    }
}
