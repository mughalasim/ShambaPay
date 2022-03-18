package ke.co.shambapay.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import ke.co.shambapay.domain.*
import ke.co.shambapay.domain.base.BaseInput
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.ui.UiGlobalState

class LoginViewModel(
    private val getLoginUseCase: GetLoginUseCase,
    private val setPasswordResetUseCase: SetPasswordResetUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val globalState: UiGlobalState
) : ViewModel() {

    val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email

    val _password = MutableLiveData<String?>()
    val password: LiveData<String?> = _password

    val _canLogIn = MutableLiveData<Boolean>()
    val canLogIn: LiveData<Boolean> = _canLogIn

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canLogIn.postValue(false)
        globalState.clear()

        if (FirebaseAuth.getInstance().currentUser != null){
            fetchUserEntity()
        }
    }

    fun validateLogin(email: String?, password: String?){
        if (email.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Email cannot be empty"))
            _canLogIn.postValue(false)
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _state.postValue(BaseState.UpdateUI(false, "Email is not valid"))
            _canLogIn.postValue(false)
            return
        }
        if (password.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Password cannot be empty"))
            _canLogIn.postValue(false)
            return
        }
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canLogIn.postValue(true)
        _email.postValue(email)
        _password.postValue(password)
    }

    fun validateEmail(email: String?){
        if (email.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Email cannot be empty"))
            _canLogIn.postValue(false)
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _state.postValue(BaseState.UpdateUI(false, "Email is not valid"))
            _canLogIn.postValue(false)
            return
        }
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canLogIn.postValue(true)
        _email.postValue(email)
    }

    fun makeLoginRequest() {
        _state.postValue(BaseState.UpdateUI(true, "Logging in, Please wait..."))
        getLoginUseCase.invoke(viewModelScope, GetLoginUseCase.Input(email.value!!, password.value!!)){

            it.result(onSuccess = {
                _state.postValue(BaseState.UpdateUI(false, ""))
                fetchUserEntity()

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(BaseState.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(BaseState.UpdateUI(false, "Unknown error when authenticating, please check back later"))}
                }
            })
        }
    }

    fun makePasswordRequest() {
        _state.postValue(BaseState.UpdateUI(true, "Verifying email, Please wait..."))
        setPasswordResetUseCase.invoke(viewModelScope, email.value!!){
            it.result(onSuccess = {
                _state.postValue(BaseState.UpdateUI(false, ""))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(BaseState.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(BaseState.UpdateUI(false, "Unknown error, please contact your administrator"))}
                }
            })
        }
    }

    private fun fetchUserEntity(){
        _state.postValue(BaseState.UpdateUI(true, "Fetching your details, Please wait..."))
        getUserUseCase.invoke(viewModelScope, BaseInput.Empty){
            it.result(onSuccess = {user ->
                globalState.user = user
                fetchCompanySettings()

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(BaseState.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(BaseState.UpdateUI(false, "Unknown error when fetching your details, please check back later"))}
                }
            })
        }
    }

    private fun fetchCompanySettings(){
        _state.postValue(BaseState.UpdateUI(true, "Fetching company settings, Please wait..."))
        getSettingsUseCase.invoke(viewModelScope, Unit){
            it.result(onSuccess = {settings ->
                globalState.settings = settings
                _state.postValue(BaseState.UpdateUI(false, ""))
                _state.postValue(BaseState.Success(globalState.user!!.userType))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(BaseState.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(BaseState.UpdateUI(false, "Unknown error when fetching company settings, please check back later"))}
                }
            })
        }
    }

}