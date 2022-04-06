package ke.co.shambapay.ui.employees

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.DeleteEmployeeUseCase
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.SetEmployeeUseCase
import ke.co.shambapay.domain.base.BaseState
import ke.co.shambapay.utils.isInValidPhone
import java.util.*
import java.util.regex.Pattern

class EmployeeUpdateViewModel(
    private val setEmployeeUseCase: SetEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase
) : ViewModel() {

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    private val _entity = MutableLiveData<EmployeeEntity>()
    private val entity: LiveData<EmployeeEntity> = _entity

    private val _companyId = MutableLiveData<String>()
    val companyId: LiveData<String> = _companyId

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
    }

    fun setEntity(entity: EmployeeEntity){
        _entity.postValue(entity)
    }

    fun setCompanyId(companyId: String){
        _companyId.postValue(companyId)
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun validate(
        firstName: String?,
        lastName: String?,
        telephone: String?
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

        _canSubmit.postValue(true)
        _state.postValue(BaseState.UpdateUI(false, ""))

    }

    fun updateEmployee (
        firstName: String,
        lastName: String,
        telephone: String,
        nationalId: String?,
        nhif: String?,
        nssf: String?
    ) {
        _entity.postValue(
            EmployeeEntity(
                id = if (entity.value?.id == null) UUID.randomUUID()
                    .toString() else entity.value!!.id,
                nationalId = nationalId?.toLongOrNull(),
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                nhif = nhif ?: "",
                nssf = nssf ?: "",
                phone = telephone.toLong()
            ))
        _state.postValue(BaseState.UpdateUI(true, "Sending details to the server, Please wait..."))
        setEmployeeUseCase.invoke(viewModelScope, SetEmployeeUseCase.Input(entity.value!!, companyId.value!!)){

            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {
                        _state.postValue(BaseState.UpdateUI(false, failure.message))
                    }

                    else -> {
                        _state.postValue(BaseState.UpdateUI(false, "Unknown error when authenticating, please check back later"))
                    }
                }
            })
        }
    }

    fun deleteEmployee() {
        _state.postValue(BaseState.UpdateUI(true, "Sending details to the server, Please wait..."))
        deleteEmployeeUseCase.invoke(viewModelScope, DeleteEmployeeUseCase.Input(entity.value!!, companyId.value!!)){

            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {
                        _state.postValue(BaseState.UpdateUI(false, failure.message))
                    }

                    else -> {
                        _state.postValue(BaseState.UpdateUI(false, "Unknown error when authenticating, please check back later"))
                    }
                }
            })
        }
    }

}