package checker.util.luis.vouchers

import checker.util.luis.vouchers.model.Balance
import checker.util.luis.vouchers.utils.exception.*
import checker.util.luis.vouchers.utils.gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


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

    fun getBalance(cardNumber: String) : Balance {
        val url = "https://bd.finutil.com.mx:6443/FinutilSite/rest/cSaldos/actual"
        val form = FormBody.Builder()
            .add("TARJETA" , cardNumber)
            .build()

        return Request.Builder()
            .url(url)
            .post(form)
            .build()
            .execute()
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
