package ke.co.shambapay.ui.capture

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.GetUserUseCase
import ke.co.shambapay.domain.GetWorkUseCase
import ke.co.shambapay.domain.SetUserUseCase
import ke.co.shambapay.ui.BaseViewModel
import org.joda.time.DateTime

class CaptureViewModel(
    val setWorkUseCase: SetWorkUseCase,
    getUserUseCase: GetUserUseCase
) : BaseViewModel(getUserUseCase) {

    val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    val _companyName = MutableLiveData<String>()
    val companyName: LiveData<String> = _companyName

    val _telephone = MutableLiveData<String>()
    val telephone: LiveData<String> = _telephone

    val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> = _firstName

    val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> = _lastName

    val _canRegister = MutableLiveData<Boolean>()
    val canRegister: LiveData<Boolean> = _canRegister

    sealed class State {
        data class UpdateUI(val showLoading: Boolean, val message: String): State()
        object Success: State()
    }

    init {
        fetchUser()
        _state.postValue(State.UpdateUI(false, ""))
        _canRegister.postValue(false)
    }

    fun validate(
        email: String?,
        firstName: String?,
        lastName: String?,
        companyName: String?,
        telephone: String?
    ){
        _canRegister.postValue(false)

        if (email.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Email cannot be empty"))
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _state.postValue(State.UpdateUI(false, "Email is not valid"))
            return
        }
        if (firstName.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "First name cannot be empty"))
            return
        }

        if (lastName.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Last name cannot be empty"))
            return
        }

        if (companyName.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Company name cannot be empty"))
            return
        }

        if (telephone.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Telephone cannot be empty"))
            return
        }

        if (telephone.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Telephone cannot be empty"))
            return
        }

        if (!Patterns.PHONE.matcher(telephone).matches()) {
            _state.postValue(State.UpdateUI(false, "Telephone is invalid"))
            return
        }

        _canRegister.postValue(true)
        _state.postValue(State.UpdateUI(false, ""))

        _email.postValue(email!!)
        _companyName.postValue(companyName!!)
        _firstName.postValue(firstName!!)
        _lastName.postValue(lastName!!)
        _telephone.postValue(telephone!!)
    }

    fun registerUser() {
        if (!hasUser()) {
            _state.postValue(State.UpdateUI(false, "Your session has expired please login again"))
            return
        }

        val userEntity = UserEntity (
            id = "",
            companyId = companyName.value + DateTime.now(),
            firstName = firstName.value!!,
            lastName = lastName.value!!,
            email = email.value!!,
            phone = telephone.value!!.toLong(),
            areaCode = 254,
            userType = UserType.OWNER,
            fcmToken = "null"
        )

        _state.postValue(State.UpdateUI(true, "Setting up user, Please wait..."))
        setUserUseCase.invoke(viewModelScope, userEntity){

            it.result(onSuccess = {
                _state.postValue(State.Success)

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(
                        State.UpdateUI(
                            false,
                            failure.message
                        )
                    )}

                    else ->{_state.postValue(
                        State.UpdateUI(
                            false,
                            "Unknown error when authenticating, please check back later"
                        )
                    )}
                }
            })
        }
    }

}