package ke.co.shambapay.domain

sealed interface Failures {
    data class WithMessage(val message: String): Failures
    object NoNetwork: Failures
    object NotAuthenticated: Failures
    object NotFound: Failures
    object UnknownException: Failures
}
