package ke.co.shambapay.domain.utils

sealed interface Failures {
    data class WithMessage(val message: String? = "An unknown error has occurred. Please contact your administrator"): Failures
    object NotAuthenticated: Failures
}
