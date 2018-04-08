package checker.util.luis.vouchers.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.repository.BalanceRepository

class BalanceViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = BalanceRepository(application)
    val allEntries: LiveData<List<BalanceEntity>> = mRepository.allRecords

    fun insert(vararg balance: BalanceEntity) {
        balance.forEach { record -> mRepository.insert(record) }
    }

    fun insert(balanceList: List<BalanceEntity>) {
        mRepository.insert(balanceList)
    }

    fun getByName(name: String): LiveData<List<BalanceEntity>> {
        return mRepository.getByName(name)
    }

    fun getDesc(): LiveData<List<BalanceEntity>> {
        return mRepository.getDesc()
    }

    fun getDesc(limit : Int): LiveData<List<BalanceEntity>> {
        return mRepository.getDesc(limit)
    }

    fun delete(balance: BalanceEntity) {
        mRepository.delete(balance)
    }

    fun deleteAll() {
        mRepository.deleteAll()
    }

    fun addRecord(newRecord: BalanceEntity) { // TODO add test main thread execution
        mRepository.addRecord(newRecord)
    }

    fun fetchData(){
        mRepository.fetchData(getApplication())
    }
}