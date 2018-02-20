package checker.util.luis.vouchers.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity(tableName = "balance_history")
class BalanceEntity(
    //TODO : this will require a type converter
//    @field:PrimaryKey
//    @field:ColumnInfo(name = "word")
//    val id: UUID = UUID.randomUUID(),

    @field:PrimaryKey
    @field:ColumnInfo(name = "name")
    var name: String = "",

    @field:ColumnInfo(name = "amount")
    var amount: String = "",

    @field:ColumnInfo(name = "lastUpdated")
    var lastUpdated: String = ""
)