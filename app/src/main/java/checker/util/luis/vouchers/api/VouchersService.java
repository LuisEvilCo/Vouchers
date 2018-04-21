package checker.util.luis.vouchers.api;

import android.arch.lifecycle.LiveData;

import checker.util.luis.vouchers.database.entity.BalanceEntity;
import retrofit2.http.POST;

public interface VouchersService {
    //https://demo7473136.mockable.io/

    @POST("finutil")
    LiveData<ApiResponse<BalanceEntity>>getBalance();
}
