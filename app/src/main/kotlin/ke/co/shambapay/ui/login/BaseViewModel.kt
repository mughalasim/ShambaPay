package ke.co.shambapay.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.GetUserUseCase
import ke.co.shambapay.domain.base.BaseInput

abstract class BaseViewModel(
    val getUserUseCase: GetUserUseCase
) : ViewModel() {

    val _userEntity = MutableLiveData<UserEntity?>()
    val userEntity: LiveData<UserEntity?> = _userEntity

    fun hasUser():Boolean {
       return userEntity.value != null
    }

    fun fetchUser(){
        getUserUseCase.invoke(viewModelScope, BaseInput.EmptyInput){
            it.result(onSuccess = {user ->
                _userEntity.postValue(user)

            }, onFailure = { failure ->
                _userEntity.postValue(null)
            })
        }
    }
}