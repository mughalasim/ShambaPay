package ke.co.shambapay.domain.utils

sealed interface Failures {
    data class WithMessage(val message: String): Failures
    object NotAuthenticated: Failures
}
