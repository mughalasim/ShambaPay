package ke.co.shambapay.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetSettingsUseCase
import ke.co.shambapay.domain.base.BaseState

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    val _data = MutableLiveData<List<EmployeeEntity>>()
    val data: LiveData<List<EmployeeEntity>> = _data


    fun refreshData() {
        _state.postValue(BaseState.UpdateUI(true, "Fetching rates, Please wait..."))

        getSettingsUseCase.invoke(viewModelScope, Unit){
            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {
                        _state.postValue(BaseState.UpdateUI(false, failure.message))
                    }

                    else ->{
                        _state.postValue(BaseState.UpdateUI(false, "Unknown error when authenticating, please check back later"))
                    }
                }
            })
        }
    }

}