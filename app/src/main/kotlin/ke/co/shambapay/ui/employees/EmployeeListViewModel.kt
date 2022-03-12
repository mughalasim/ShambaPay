package ke.co.shambapay.ui.employees

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetEmployeesUseCase
import ke.co.shambapay.domain.base.BaseState

class EmployeeListViewModel(
    val getEmployeesUseCase: GetEmployeesUseCase
) : ViewModel() {

    val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    val _data = MutableLiveData<List<EmployeeEntity>>()
    val data: LiveData<List<EmployeeEntity>> = _data


    fun getRecyclerData(textFilter: String?) {
        _state.postValue(BaseState.UpdateUI(true, "Fetching employees, Please wait..."))

        getEmployeesUseCase.invoke(viewModelScope, textFilter ?: ""){
            it.result(onSuccess = {
                if (it.isEmpty()){
                    _state.postValue(BaseState.UpdateUI(false, "No results found"))
                } else {
                    _state.postValue(BaseState.UpdateUI(false, ""))
                }
                _data.postValue(it)

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