package ke.co.shambapay.ui.employees

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.GetEmployeesUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel

class EmployeeListViewModel(
    private val getEmployeesUseCase: GetEmployeesUseCase
) : BaseViewModel() {

    private val _data = MutableLiveData<List<EmployeeEntity>>()
    val data: LiveData<List<EmployeeEntity>> = _data

    fun getEmployees(companyId: String? = null, textFilter: String?) {
        _state.postValue(BaseState.UpdateUI(true, "Fetching employees, Please wait..."))

        getEmployeesUseCase.invoke(viewModelScope, GetEmployeesUseCase.Input(companyId, textFilter ?: "")){
            it.result(onSuccess = { list ->
                if (list.isEmpty()){
                    _state.postValue(BaseState.UpdateUI(false, "No results found"))
                } else {
                    _state.postValue(BaseState.UpdateUI(false, ""))
                }
                _data.postValue(list)

            }, onFailure = { failure ->
                handleFailure(failure)
            })
        }
    }

}