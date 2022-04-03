package ke.co.shambapay.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.SetCompanyNameUseCase
import ke.co.shambapay.domain.base.BaseState

class SettingsViewModel(
    private val setCompanyNameUseCase: SetCompanyNameUseCase
) : ViewModel() {

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
    }

    fun setCompanyName(companyName: String) {
        _state.postValue(BaseState.UpdateUI(true, "Updating name, Please wait..."))
        setCompanyNameUseCase.invoke(viewModelScope, companyName) {
            it.result(
                onSuccess = { _state.postValue(BaseState.Success(companyName)) },

                onFailure = { failure ->
                    when (failure) {
                        is Failures.WithMessage -> { _state.postValue(BaseState.UpdateUI(false, failure.message)) }

                        else -> { _state.postValue(BaseState.UpdateUI(false, "Unknown error when authenticating, please check back later")) }
                    }
                }
            )
        }
    }
}