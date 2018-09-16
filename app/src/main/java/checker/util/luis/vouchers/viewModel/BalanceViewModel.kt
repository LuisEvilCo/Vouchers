package checker.util.luis.vouchers.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.repository.BalanceRepository
import checker.util.luis.vouchers.vo.Resource

class BalanceViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = BalanceRepository(application)
    // NetworkBoundResources implementation
    fun getDescResource(): LiveData<Resource<List<BalanceEntity>>> {
        return mRepository.getDescResource()
    }
}