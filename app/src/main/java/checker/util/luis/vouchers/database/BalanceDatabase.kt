package checker.util.luis.vouchers.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity


@Database(entities = [(BalanceEntity::class)], version = 1)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun balanceDao(): BalanceDao

    companion object {
        private var INSTANCE: BalanceDatabase? = null

        fun getDatabase(context: Context): BalanceDatabase {
            if (INSTANCE == null) {
                synchronized(BalanceEntity::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            BalanceDatabase::class.java,
                            "balance_database"
                        )
                            .addCallback(sRoomDatabaseCallback)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(INSTANCE!!).execute()
            }
        }
    }

    private class PopulateDbAsync internal constructor(db: BalanceDatabase) :
        AsyncTask<Void, Void, Void>() {

        private val mDao: BalanceDao = db.balanceDao()

        override fun doInBackground(vararg voids: Void): Void? {
            mDao.deleteAll()
            var entity = BalanceEntity("Luis", "$89.99", "")
            mDao.insert(entity)
            entity = BalanceEntity("Luis T", "$85.2", "")
            mDao.insert(entity)
            return null
        }
    }

}
