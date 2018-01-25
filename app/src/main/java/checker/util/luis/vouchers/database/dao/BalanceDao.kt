package checker.util.luis.vouchers.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import checker.util.luis.vouchers.database.entity.BalanceEntity


@Dao
public interface BalanceDao {
    @get:Query("SELECT * FROM balance")
    val all : List<BalanceEntity>

    @Insert
    fun insertOne(balance : BalanceEntity)

    @Insert
    fun insertAll(vararg balance : BalanceEntity)

    @Delete
    fun delete(balance : BalanceEntity)
}