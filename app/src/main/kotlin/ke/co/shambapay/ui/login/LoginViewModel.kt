package ke.co.shambapay.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetLoginUseCase

class LoginViewModel(
    val getLoginUseCase: GetLoginUseCase
) : ViewModel() {

    val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    sealed class State {
        data class Loading(val show: Boolean): State()
        data class ShowMessage(val message: String): State()
        data class LoggedInUser(val user: UserEntity): State()

    }


    fun makeLoginRequest(email: String, password: String) {
        _state.postValue(State.Loading(true))
        getLoginUseCase.invoke(viewModelScope ,GetLoginUseCase.Input(email, password)){

            it.result(onSuccess = { user ->
                _state.postValue(State.Loading(false))
                _state.postValue(State.LoggedInUser(user))

            }, onFailure = { failure ->
                _state.postValue(State.Loading(false))
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(State.ShowMessage(failure.message))}

                    is Failures.NotAuthenticated -> {_state.postValue(State.ShowMessage("Invalid username or password"))}

                    else ->{_state.postValue(State.ShowMessage("Unknown error, please check back later"))}
                }
            })
        }

    }

}