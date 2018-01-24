package checker.util.luis.vouchers.utils.exception

abstract class VoucherException(
    message: String? = "Internal Error",
    cause: Throwable? = null
) : Exception(message, cause) {
    abstract val statusCode: Int
}