package checker.util.luis.vouchers.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.repository.BalanceRepository

class BalanceViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = BalanceRepository(application)
    val allEntries: LiveData<List<BalanceEntity>> = mRepository.allRecords

    fun insert(balance: BalanceEntity) {
        mRepository.insert(balance)
    }

    fun getByName(name: String): LiveData<List<BalanceEntity>> {
        return mRepository.getByName(name)
    }

    fun getDesc(): LiveData<List<BalanceEntity>> {
        return mRepository.getDesc()
    }
}