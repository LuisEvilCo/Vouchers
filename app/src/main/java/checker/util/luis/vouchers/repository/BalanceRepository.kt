package checker.util.luis.vouchers.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import checker.util.luis.vouchers.database.BalanceDatabase
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity


class BalanceRepository (application: Application) {
    //private val db = AppDatabase.getDatabase(application)
    private val db = BalanceDatabase.getDatabase(application)
    private val mBalanceDao : BalanceDao = db.balanceDao()
    val allRecords : LiveData<List<BalanceEntity>> = mBalanceDao.allRecords

    fun insert(balance : BalanceEntity) {
        InsertAsyncTask(mBalanceDao).execute(balance)
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: BalanceDao) :
            AsyncTask<BalanceEntity, Void, Void>() {
        override fun doInBackground(vararg params: BalanceEntity?): Void? {
            params[0]?.let { mAsyncTaskDao.insert(it) }
            return null
        }
    }
}