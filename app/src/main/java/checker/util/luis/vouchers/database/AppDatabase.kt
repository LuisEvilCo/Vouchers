package checker.util.luis.vouchers.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity


@Database(entities = arrayOf(BalanceEntity::class), version = 1)
// arrayOf can be replaced with array literal '[(BalanceEntity::class)]'
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
}