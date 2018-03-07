package checker.util.luis.vouchers.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import checker.util.luis.vouchers.database.BalanceDatabase
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity


class BalanceRepository (application: Application) {

    private val db = BalanceDatabase.getDatabase(application)
    private val mBalanceDao : BalanceDao = db.balanceDao()
    val allRecords : LiveData<List<BalanceEntity>> = mBalanceDao.allRecords


    fun insert(vararg balance: BalanceEntity) {
        balance.forEach { record -> InsertAsyncTask(mBalanceDao).execute(record) }
    }

    fun insert(balanceList: List<BalanceEntity>) {
        balanceList.forEach { record -> InsertAsyncTask(mBalanceDao).execute(record) }
    }

    fun getByName(nameString : String): LiveData<List<BalanceEntity>> {
        return mBalanceDao.getByName(nameString)
    }

    fun getDesc() : LiveData<List<BalanceEntity>> {
        return mBalanceDao.getDescendant()
    }

    fun getDesc(limit: Int) : LiveData<List<BalanceEntity>> {
        return mBalanceDao.getDescendant(limit)
    }

    fun delete(balance: BalanceEntity) {
        mBalanceDao.delete(balance)
    }

    fun deleteAll() {
        mBalanceDao.deleteAll()
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: BalanceDao) :
            AsyncTask<BalanceEntity, Void, Void>() {
        override fun doInBackground(vararg params: BalanceEntity): Void? {
            params.forEach { it -> mAsyncTaskDao.insert(it) }
            return null
        }
    }
}