package checker.util.luis.vouchers.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.os.AsyncTask
import checker.util.luis.vouchers.database.typeConverters.CustomTypeConverters
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity


@Database(entities = [(BalanceEntity::class)], version = 1)
@TypeConverters(CustomTypeConverters::class)
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
            //mDao.deleteAll()
//            val myTime = "14:10"
//            val df = SimpleDateFormat("HH:mm")
//            val d = df.parse(myTime)
//            val cal = Calendar.getInstance()
//            cal.time = d
//            cal.add(Calendar.MINUTE, 10)
//            val newTime = df.format(cal.time)
//
//            var entity = BalanceEntity(name = "Luis", amount = "$89.99", lastUpdated = cal.time )
//            mDao.insert(entity)
//
//            cal.add(Calendar.YEAR, 99)
//            entity = BalanceEntity(name = "Luis T", amount = "$85.2", lastUpdated = cal.time)
//            mDao.insert(entity)
//            entity = BalanceEntity(name = "LuisA", amount = "$0")
//            mDao.insert(entity)
//            entity = BalanceEntity(name = "LuisB", amount = "$0.1")
//            mDao.insert(entity)
            return null
        }
    }

}
