package checker.util.luis.vouchers.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import checker.util.luis.vouchers.R
import checker.util.luis.vouchers.VoucherClient
import checker.util.luis.vouchers.database.BalanceDatabase
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity
import org.jetbrains.anko.doAsync


class BalanceRepository(application: Application) {

    companion object {
        private const val TAG = "balance_repository"
    }

    private val db = BalanceDatabase.getDatabase(application)
    private val mBalanceDao: BalanceDao = db.balanceDao()
    private val mContext: Context? = application.applicationContext
    val allRecords: LiveData<List<BalanceEntity>> = mBalanceDao.allRecords

    fun insert(vararg balance: BalanceEntity) {
        balance.forEach { record -> InsertAsyncTask(mBalanceDao).execute(record) }
    }

    fun insert(balanceList: List<BalanceEntity>) {
        balanceList.forEach { record -> InsertAsyncTask(mBalanceDao).execute(record) }
    }

    fun getByName(nameString: String): LiveData<List<BalanceEntity>> {
        return mBalanceDao.getByName(nameString)
    }

    fun getDesc(): LiveData<List<BalanceEntity>> {
        mContext?.let { fetchData(it) } ?: Log.w(TAG,"null application context")
        return mBalanceDao.getDescendantAsync()
    }

    fun getDesc(limit: Int): LiveData<List<BalanceEntity>> {
        mContext?.let { fetchData(it) } ?: Log.w(TAG,"null application context")
        return mBalanceDao.getDescendantAsync(limit)
    }

    fun delete(balance: BalanceEntity) {
        mBalanceDao.delete(balance)
    }

    fun deleteAll() {
        mBalanceDao.deleteAll()
    }

    fun getLatest() : BalanceEntity? {
        val list: List<BalanceEntity> = mBalanceDao.getDescendantSync(1)
        return if (list.isNotEmpty()) {
            list.first()
        }else {
            null
        }
    }

    fun addRecord(newRecord: BalanceEntity) {
        getLatest()?.let { it ->
            if (it.hasChange(newRecord)) {
                this.insert(newRecord)
            }
        } ?: this.insert(newRecord)
    }

    fun fetchData(context: Context) {
        var balanceEntity: BalanceEntity?
        doAsync {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.string_preference_file_key),
                Context.MODE_PRIVATE
            )
            val card: String = sharedPref.getString(context.getString(R.string.card), "")

            if (card.isNotEmpty()) {
                balanceEntity = VoucherClient.getBalanceEntity(card)
                balanceEntity?.let { notNullCall -> addRecord(notNullCall) }
            }
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