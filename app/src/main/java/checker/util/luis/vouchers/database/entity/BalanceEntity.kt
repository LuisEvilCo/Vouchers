package checker.util.luis.vouchers.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class BalanceEntity {
    @PrimaryKey
    private val id : UUID = UUID.randomUUID()

    @ColumnInfo(name = "name")
    private val name : String = ""

    @ColumnInfo(name = "amount")
    private val amount : String = ""

    @ColumnInfo(name = "lastUpdated")
    private val lastUpdated : String = ""
}