package checker.util.luis.vouchers.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.repository.BalanceRepository

class BalanceViewModel(application: Application) : AndroidViewModel(application) {
    private val mRespository = BalanceRepository(application)
    val allEntries: LiveData<List<BalanceEntity>> = mRespository.allRecords

    fun insert(balance: BalanceEntity) {
        mRespository.insert(balance)
    }
}