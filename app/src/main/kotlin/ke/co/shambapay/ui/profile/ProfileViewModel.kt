package ke.co.shambapay.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.*
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.UiGlobalState
import org.joda.time.DateTime

class ProfileViewModel(
    private val setPasswordUseCase: SetPasswordUseCase
): ViewModel() {

    val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    val _newPassword = MutableLiveData<String>()
    val newPassword: LiveData<String> = _newPassword

    val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
        _newPassword.postValue("")
        _confirmPassword.postValue("")
    }

    fun validatePassword (
        newPassword: String?,
        confirmPassword: String?
    ){
        _canSubmit.postValue(false)

        if (newPassword.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Please set your new password"))
            return
        }
        if (confirmPassword.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Please set your confirmed password"))
            return
        }
        if (confirmPassword != newPassword) {
            _state.postValue(BaseState.UpdateUI(false, "Your new password and confirmed password do not match"))
            return
        }

        _canSubmit.postValue(true)
        _state.postValue(BaseState.UpdateUI(false, ""))

        _newPassword.postValue(newPassword!!)
        _confirmPassword.postValue(confirmPassword!!)
    }

    fun updatePassword(password: String) {

        _state.postValue(BaseState.UpdateUI(true, "Updating your password, Please wait..."))
        setPasswordUseCase.invoke(viewModelScope, password){
            it.result(
                onSuccess = {
                    _state.postValue(BaseState.Success(Unit))
                    _state.postValue(BaseState.UpdateUI(false, "Password update successful"))
                },
                onFailure = { failure ->
                    when(failure){
                    is Failures.WithMessage -> {_state.postValue(
                        BaseState.UpdateUI(
                            false,
                            failure.message
                        )
                    )}

                    else ->{_state.postValue(
                        BaseState.UpdateUI(
                            false,
                            "Unknown error when authenticating, please check back later"
                        )
                    )}
                }
                }
            )
        }
    }
}