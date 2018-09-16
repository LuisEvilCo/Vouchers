package checker.util.luis.vouchers.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import checker.util.luis.vouchers.network.NetworkClient
import checker.util.luis.vouchers.R
import checker.util.luis.vouchers.api.ApiResponse
import checker.util.luis.vouchers.database.BalanceDatabase
import checker.util.luis.vouchers.database.dao.BalanceDao
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.utils.AbsentLiveData
import checker.util.luis.vouchers.vo.AppExecutors
import checker.util.luis.vouchers.vo.Resource


class BalanceRepository(application: Application) {

    companion object {
        private const val TAG = "balance_repository"
    }

    private val db = BalanceDatabase.getDatabase(application)
    private val mBalanceDao: BalanceDao = db.balanceDao()
    private val mContext: Context? = application.applicationContext
    private val networkClient = NetworkClient.api
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

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: BalanceDao) :
        AsyncTask<BalanceEntity, Void, Void>() {
        override fun doInBackground(vararg params: BalanceEntity): Void? {
            params.forEach { it -> mAsyncTaskDao.insert(it) }
            return null
        }
    }


    // Network Bound Resource
    fun getDescResource(): LiveData<Resource<List<BalanceEntity>>> {
        return object : NetworkBoundResource<List<BalanceEntity>, BalanceEntity>(AppExecutors.getInstance()) {
            override fun saveCallResult(item: BalanceEntity) {
                addRecord(item)
            }

            override fun shouldFetch(data: List<BalanceEntity>?): Boolean {
                // individually decide if we should go to network depending on request type
                return true
            }

            override fun loadFromDb(): LiveData<List<BalanceEntity>> {
                // db read
                return mBalanceDao.getDescendantAsync()
            }

            override fun createCall(): LiveData<ApiResponse<BalanceEntity>> {
                mContext?.let {
                    val sharedPref = mContext.getSharedPreferences(
                        mContext.getString(R.string.string_preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    val card: String = sharedPref.getString(mContext.getString(R.string.card), "")
                    if (card.isNotEmpty()) {
                        return networkClient.getBalanceEntity(card)
                    }
                }
                Log.e(TAG, "Could not get app context")
                return AbsentLiveData.create()
            }
        }.asLiveData()
    }
}