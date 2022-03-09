package ke.co.shambapay.domain.base

sealed class BaseState {
    data class UpdateUI(val showLoading: Boolean, val message: String): BaseState()
    data class Success<T>(val data: T): BaseState()
}