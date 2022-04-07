package ke.co.shambapay.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetUsersUseCase
import ke.co.shambapay.domain.SetCompanyIdUseCase
import ke.co.shambapay.domain.base.BaseState

class UserListViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val setCompanyIdUseCase: SetCompanyIdUseCase
) : ViewModel() {

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> = _users


    fun getAllUsers(companyId: String) {
        _state.postValue(BaseState.UpdateUI(true, "Fetching all users in this company, Please wait..."))

        getUsersUseCase.invoke(viewModelScope, companyId){
            it.result(onSuccess = { list ->
                if (list.isEmpty()){
                    _state.postValue(BaseState.UpdateUI(false, "No results found"))
                } else {
                    _state.postValue(BaseState.UpdateUI(false, ""))
                }
                _users.postValue(list)

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

    fun setDefaultCompanyId(companyId: String){
        _state.postValue(BaseState.UpdateUI(true, "Setting your company ID, you will need to login again..."))

        setCompanyIdUseCase.invoke(viewModelScope, companyId){
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