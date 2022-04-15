package ke.co.shambapay.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ke.co.shambapay.domain.utils.Failures

open class BaseViewModel: ViewModel() {

    val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    fun handleFailure(failure: Failures){
        when (failure) {
            is Failures.WithMessage -> {
                _state.postValue(BaseState.UpdateUI(false, failure.message ?: ""))
            }
            is Failures.NotAuthenticated ->{
                _state.postValue(BaseState.Logout)
            }
        }
    }
}