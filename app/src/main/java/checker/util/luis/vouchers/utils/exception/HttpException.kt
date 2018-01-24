package checker.util.luis.vouchers.utils.exception

class BadRequestException(
    message: String? = null,
    cause: Throwable? = null
) : VoucherException(message, cause) {
    override val statusCode: Int
        get() = 400
}

class UnauthorizedException(
    message: String? = null,
    cause : Throwable? = null
) : VoucherException(message, cause) {
    override val statusCode: Int
        get() = 401
}

class NotFoundException(
    message: String? = null,
    cause: Throwable? = null
) : VoucherException(message, cause) {
    override val statusCode: Int
        get() = 404
}

class InternalErrorException(
    message: String? = null,
    cause: Throwable? = null
) : VoucherException(message, cause) {
    override val statusCode: Int
        get() = 500
}