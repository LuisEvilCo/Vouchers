package checker.util.luis.vouchers.api;

import android.arch.lifecycle.LiveData;

import checker.util.luis.vouchers.database.entity.BalanceEntity;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface VouchersApi {

    @FormUrlEncoded
    @POST("https://bd.finutil.com.mx:6443/FinutilSite/rest/cSaldos/actual")
    LiveData<ApiResponse<BalanceEntity>> getBalanceEntity(@Field("TARJETA") String cardNumber);

}
