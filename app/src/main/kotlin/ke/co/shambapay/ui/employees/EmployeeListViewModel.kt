package ke.co.shambapay.ui.employees

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetEmployeesUseCase
import ke.co.shambapay.domain.GetUserUseCase
import ke.co.shambapay.ui.BaseViewModel

class EmployeeListViewModel(
    val getEmployeesUseCase: GetEmployeesUseCase,
    getUserUseCase: GetUserUseCase
) : BaseViewModel(getUserUseCase) {

    val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    val _data = MutableLiveData<List<EmployeeEntity>>()
    val data: LiveData<List<EmployeeEntity>> = _data

    sealed class State {
        data class UpdateUI(val showLoading: Boolean, val message: String): State()
        object Success: State()
    }

    init {
        fetchUser()
    }


    fun getRecyclerData(textFilter: String?) {
//        if (!hasUser()) {
//            _state.postValue(State.UpdateUI(false, "Your session has expired please login again"))
//            return
//        }

        _state.postValue(State.UpdateUI(true, "Fetching employees, Please wait..."))

        getEmployeesUseCase.invoke(
            viewModelScope,
            GetEmployeesUseCase.Input.Filtered(
            UserEntity(
                id = "",
                companyId = "1234",
                firstName = "",
                lastName = "",
                email = "",
                phone = 0,
                areaCode = 0,
                userType = UserType.ADMIN,
                fcmToken = ""),
                textFilter ?: "")

        ){
            it.result(onSuccess = {
                _state.postValue(State.UpdateUI(false, ""))
                _data.postValue(it)

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {
                        _state.postValue(State.UpdateUI(false, failure.message))
                    }

                    else ->{
                        _state.postValue(State.UpdateUI(false, "Unknown error when authenticating, please check back later"))
                    }
                }
            })
        }
    }

}