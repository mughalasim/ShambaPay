package ke.co.shambapay.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.domain.DeleteUserAndCompanyUseCase
import ke.co.shambapay.domain.RegisterUserUseCase
import ke.co.shambapay.domain.SetCompanyUseCase
import ke.co.shambapay.domain.SetUserUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel
import ke.co.shambapay.utils.isInValidPhone
import ke.co.shambapay.utils.isInvalidEmail

class UserUpdateViewModel(
    private val setUserUseCase: SetUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val setCompanyUseCase: SetCompanyUseCase,
    private val deleteUserAndCompanyUseCase: DeleteUserAndCompanyUseCase
) : BaseViewModel() {

    private var entity: UserEntity? = null

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
    }

    fun setEntity(user: UserEntity?){
        entity = user
    }

    fun validate (
        firstName: String?,
        lastName: String?,
        telephone: String?,
        email: String?,
        companyName: String?,
        userType: UserType
    ){
        _canSubmit.postValue(false)

        if (firstName.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "First name cannot be empty"))
            return
        }

        if (lastName.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Last name cannot be empty"))
            return
        }

        if (telephone.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Telephone cannot be empty"))
            return
        }

        if (telephone.isInValidPhone()) {
            _state.postValue(BaseState.UpdateUI(false, "Telephone is invalid and must start with the area code"))
            return
        }

        if (email.isNullOrEmpty() || email.isInvalidEmail()) {
            _state.postValue(BaseState.UpdateUI(false, "Invalid email address"))
            return
        }

        if (userType == UserType.OWNER){
            if (companyName.isNullOrEmpty()){
                _state.postValue(BaseState.UpdateUI(false, "Company name cannot be empty"))
                return
            }
        }

        _canSubmit.postValue(true)
        _state.postValue(BaseState.UpdateUI(false, ""))

    }

    fun updateUser (
        firstName: String,
        lastName: String,
        telephone: String,
        email: String,
        companyName: String?,
        companyId: String,
        userType: UserType
    ) {
        val isUpdate = if (entity != null){
           entity!!.apply {
               this.firstName = firstName
               this.lastName = lastName
               this.phone = telephone.toLong()
           }
            true
        } else {
            entity =
                UserEntity(
                    id = "",
                    companyId = companyId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phone = telephone.toLong(),
                    areaCode = 254,
                    userType = userType,
                    fcmToken = "",
                )
            false
        }

        if (isUpdate){
            setUserToDatabase()
        } else {
            registerUser(companyName)
        }
    }

    fun deleteUser() {
        _state.postValue(BaseState.UpdateUI(true, "Deleting user, Please wait..."))
        deleteUserAndCompanyUseCase.invoke(viewModelScope, entity!!){

            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                handleFailure(failure)
            })
        }
    }

    private fun registerUser(companyName: String?) {
        _state.postValue(BaseState.UpdateUI(true, "Registering user, Please wait..."))
        registerUserUseCase.invoke(viewModelScope, entity!!.email){

            it.result(onSuccess = {
                entity!!.apply { id = it }
                if (companyName.isNullOrEmpty()){
                    setUserToDatabase()
                } else {
                    setCompany(companyName)
                }
            }, onFailure = { failure ->
                handleFailure(failure)
            })
        }
    }

    private fun setCompany(companyName: String) {
        _state.postValue(BaseState.UpdateUI(true, "Setting up the company, Please wait..."))
        setCompanyUseCase.invoke(viewModelScope, companyName){

            it.result(onSuccess = {
                entity!!.apply { companyId = it }
                setUserToDatabase()
            }, onFailure = { failure ->
                deleteUser()
                handleFailure(failure)
            })
        }
    }



    private fun setUserToDatabase() {
        _state.postValue(BaseState.UpdateUI(true, "Setting user to the database, Please wait..."))
        setUserUseCase.invoke(viewModelScope, entity!!){

            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                deleteUser()
                handleFailure(failure)
            })
        }
    }

}