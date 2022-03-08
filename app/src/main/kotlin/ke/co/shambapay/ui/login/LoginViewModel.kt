package ke.co.shambapay.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetLoginUseCase
import ke.co.shambapay.domain.GetUserUseCase
import ke.co.shambapay.domain.base.BaseInput

class LoginViewModel(
    val getLoginUseCase: GetLoginUseCase,
    val getUserUseCase: GetUserUseCase
) : ViewModel() {

    val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    val _canLogIn = MutableLiveData<Boolean>()
    val canLogIn: LiveData<Boolean> = _canLogIn

    sealed class State {
        data class UpdateUI(val showLoading: Boolean, val message: String): State()
        data class Success(val userType: UserType): State()
    }

    init {
        _state.postValue(State.UpdateUI(false, ""))
        _canLogIn.postValue(false)
    }

    fun validate(email: String?, password: String?){
        if (email.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Email cannot be empty"))
            _canLogIn.postValue(false)
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _state.postValue(State.UpdateUI(false, "Email is not valid"))
            _canLogIn.postValue(false)
            return
        }
        if (password.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Password cannot be empty"))
            _canLogIn.postValue(false)
            return
        }
        _state.postValue(State.UpdateUI(false, ""))
        _canLogIn.postValue(true)
        _email.postValue(email!!)
        _password.postValue(password!!)
    }

    fun makeLoginRequest() {
        _state.postValue(State.UpdateUI(true, "Logging in, Please wait..."))
        getLoginUseCase.invoke(viewModelScope, GetLoginUseCase.Input(email.value!!, password.value!!)){

            it.result(onSuccess = { user ->
                _state.postValue(State.UpdateUI(false, ""))
                fetchUserEntity()

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(State.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(State.UpdateUI(false, "Unknown error when authenticating, please check back later"))}
                }
            })
        }
    }

    private fun fetchUserEntity(){
        _state.postValue(State.UpdateUI(true, "Fetching your details, Please wait..."))
        getUserUseCase.invoke(viewModelScope, BaseInput.Empty){
            it.result(onSuccess = {user ->
                _state.postValue(State.UpdateUI(false, ""))
                _state.postValue(State.Success(user.userType))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(State.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(State.UpdateUI(false, "Unknown error when fetching your details, please check back later"))}
                }
            })
        }
    }

}