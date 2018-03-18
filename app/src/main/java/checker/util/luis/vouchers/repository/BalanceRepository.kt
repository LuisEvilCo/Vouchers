package checker.util.luis.vouchers.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.util.Log
import checker.util.luis.vouchers.database.BalanceDatabase
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.utils.LiveDataUtil
import kotlin.system.measureTimeMillis


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

    fun addRecord(newRecord: BalanceEntity) {
        val latest = LiveDataUtil
            .getValue(mBalanceDao.getDescendant(1))

        if (latest.isEmpty() || latest.first().hasChange(newRecord)) {
            this.insert(newRecord)
        }

    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: BalanceDao) :
            AsyncTask<BalanceEntity, Void, Void>() {
        override fun doInBackground(vararg params: BalanceEntity): Void? {
            params.forEach { it -> mAsyncTaskDao.insert(it) }
            return null
        }
    }
}