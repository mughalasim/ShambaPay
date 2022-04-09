package ke.co.shambapay.ui.settings

import androidx.lifecycle.viewModelScope
import ke.co.shambapay.domain.SetCompanyNameUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel

class SettingsViewModel(
    private val setCompanyNameUseCase: SetCompanyNameUseCase
) : BaseViewModel() {

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
    }

    fun setCompanyName(companyName: String) {
        _state.postValue(BaseState.UpdateUI(true, "Updating name, Please wait..."))
        setCompanyNameUseCase.invoke(viewModelScope, companyName) {
            it.result(
                onSuccess = { _state.postValue(BaseState.Success(companyName)) },

                onFailure = { failure ->
                    handleFailure(failure)
                }
            )
        }
    }
}