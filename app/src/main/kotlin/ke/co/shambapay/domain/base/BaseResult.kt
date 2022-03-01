package ke.co.shambapay.domain.base

sealed class BaseResult<out S, out F> {

    data class Success<out S>(val successType: S) : BaseResult<S, Nothing>()
    open class Failure<out F>(val errorType: F) : BaseResult<Nothing, F>()

    fun result(
        onSuccess: (S) -> Unit = {},
        onFailure: (F) -> Unit = {}

    ): Unit = when (this) {
        is Success -> onSuccess(successType)
        is Failure -> onFailure(errorType)
    }
}


