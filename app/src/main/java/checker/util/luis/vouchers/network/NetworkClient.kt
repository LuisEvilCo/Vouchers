package checker.util.luis.vouchers.network

import android.util.Log
import checker.util.luis.vouchers.BuildConfig
import checker.util.luis.vouchers.api.VouchersApi
import checker.util.luis.vouchers.factories.LiveDataCallAdapterFactory
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class NetworkClient private constructor() {
    private val retrofit: Retrofit
    private val client: OkHttpClient

    init {

        Log.d(LOG_TAG, " Creating Network Client ")

        val gson = GsonBuilder()
            //.setDateFormat() // TODO : figure this out
            .setLenient()
            .serializeNulls()
            .create()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpBuilder = OkHttpClient.Builder()

        if(BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(loggingInterceptor)
        }

        client = okHttpBuilder.build()

        retrofit = Retrofit.Builder().baseUrl("https://bd.finutil.com.mx:6443")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()

        vouchersApi = retrofit.create(VouchersApi::class.java)
    }

    companion object {
        private var instance: NetworkClient?= null
        private val LOG_TAG = NetworkClient::class.java.simpleName

        private var vouchersApi: VouchersApi?= null

        @JvmStatic
        val api: VouchersApi
            get() {
                if (instance == null) {
                    synchronized(NetworkClient::class.java) {
                        if(instance == null) {
                            instance = NetworkClient()
                        }
                    }
                }
                return vouchersApi!!
            }
    }
}