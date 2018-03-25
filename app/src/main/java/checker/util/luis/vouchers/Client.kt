package checker.util.luis.vouchers

import android.util.Log
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.utils.exception.BadRequestException
import checker.util.luis.vouchers.utils.exception.InternalErrorException
import checker.util.luis.vouchers.utils.exception.NotFoundException
import checker.util.luis.vouchers.utils.exception.UnauthorizedException
import checker.util.luis.vouchers.utils.gson
import okhttp3.*
import java.io.IOException


object VoucherClient {
    val VOUCHER_CLIENT = OkHttpClient.Builder()
        .addInterceptor{
            val original = it.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(original.method(), original.body())
                .build()
            it.proceed(request)
        }
        .build()!!

    fun getBalance(cardNumber: String) : BalanceEntity {
        val url = "https://demo7473136.mockable.io/finutil" // "{\"nombre\":\"Luis\",\n \"value\":\"89.99\"}"
        //val url = "https://bd.finutil.com.mx:6443/FinutilSite/rest/cSaldos/actual"
        val form = FormBody.Builder()
            .add("TARJETA" , cardNumber)
            .build()

        val result = Request.Builder()
            .url(url)
            .post(form)
            .build()

        result.executeAsync<BalanceEntity>()

        return  result.tryExecute() ?: BalanceEntity(name = "", amount = "")
    }

    fun getBalanceEntity(cardNumber: String) : BalanceEntity? {
        val url = "https://demo7473136.mockable.io/finutil" // "{\"nombre\":\"Luis\",\n \"value\":\"89.99\"}"
        val form = FormBody.Builder()
            .add("TARJETA", cardNumber)
            .build()

        val result = Request.Builder()
            .url(url)
            .post(form)
            .build()

        return result.tryExecute<BalanceEntity>()
    }
}

private inline fun <reified T> Request.execute() : T {
    val response = VoucherClient.VOUCHER_CLIENT.newCall(this).execute()
    val url = url().toString()
    val code = response.code()
    val stream = when (code) {
        200 -> response.body()!!.charStream() // TODO, check the assert not null
        400 -> throw BadRequestException("Url : $url")
        401 -> throw UnauthorizedException("Url : $url")
        404 -> throw NotFoundException(url)
        else -> throw InternalErrorException("Unexpected response code $code for $url")
    }
    return gson.fromJson(stream, T::class.java).apply { stream.close() }
}

private inline fun <reified T> Request.executeAsync() {
    VoucherClient.VOUCHER_CLIENT.newCall(this)
        .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("Some error my friend", e.message)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val url = url().toString()
                        val code = response.code()
                        val stream = when(code) {
                            200 -> response.body()!!.charStream() // TODO , check assert not null
                            400 -> throw  BadRequestException("Url : $url")
                            401 -> throw  UnauthorizedException("Url : $url")
                            404 -> throw NotFoundException(url)
                            else -> throw InternalErrorException("Unexpected response code $code for $url")
                        }

                        val result = gson.fromJson(stream, T::class.java).apply { stream.close() }

//                        when(result::class) { // why this is invalid ?
//                            Int::class -> ""
//                            BalanceEntity::class -> ""
//                        }

                        when(T::class) {
                            Int::class -> Log.d("HEY", "this is wrong")
                            BalanceEntity:: class -> Log.d("HEY", "it worked ?")
                        }
                    }
                }
        )
}

private inline fun <reified T> Request.tryExecute() : T? {
        try {
            val response = VoucherClient.VOUCHER_CLIENT.newCall(this)
                .execute()
            val url = url().toString()
            val code = response.code()
            val stream = when (code) {
                200 -> response.body()!!.charStream() // TODO, check the assert not null
                400 -> throw BadRequestException("Url : $url")
                401 -> {
                    throw UnauthorizedException("Url : $url")
                }
                404 -> throw NotFoundException(url)
                else -> throw InternalErrorException("Unexpected response code $code for $url")
            }
            return gson.fromJson(stream, T::class.java).apply { stream.close() }
        } catch (e : Exception){
            Log.d("Some error my friend", e.message)
            return null
        }
}