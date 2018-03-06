package checker.util.luis.vouchers.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import checker.util.luis.vouchers.database.entity.BalanceEntity
import org.intellij.lang.annotations.Language

@Dao
interface BalanceDao {
    @get:Query("SELECT * FROM balance_history")
    val allRecords : LiveData<List<BalanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(balance : BalanceEntity)

    @Insert
    fun insertAll(vararg balance : BalanceEntity)

    @Delete
    fun delete(balance : BalanceEntity)

    @Language("RoomSql")
    @Query("DELETE FROM balance_history")
    fun deleteAll()

    @Language("RoomSql")
    @Query("SELECT * FROM balance_history WHERE balance_history.name LIKE :nameString")
    // Note on the LIKE , we should use the wildcard in the val `%nameString%` not in the query itself
    fun getByName(nameString : String) : LiveData<List<BalanceEntity>>

    @Language("RoomSql")
    @Query("SELECT * FROM balance_history ORDER BY balance_history.lastUpdated DESC")
    fun getDescendant() : LiveData<List<BalanceEntity>>
}