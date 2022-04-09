package ke.co.shambapay.ui.employees

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.domain.DeleteEmployeeUseCase
import ke.co.shambapay.domain.SetEmployeeUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel
import ke.co.shambapay.utils.isInValidPhone
import java.util.*

class EmployeeUpdateViewModel(
    private val setEmployeeUseCase: SetEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase
) : BaseViewModel() {

    private var entity:  EmployeeEntity? = null

    private val _companyId = MutableLiveData<String>()
    val companyId: LiveData<String> = _companyId

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
    }

    fun setEntity(employeeEntity: EmployeeEntity){
        entity = employeeEntity
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
        entity =
            EmployeeEntity(
                id = if (entity?.id == null) UUID.randomUUID()
                    .toString() else entity!!.id,
                nationalId = nationalId?.toLongOrNull(),
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                nhif = nhif ?: "",
                nssf = nssf ?: "",
                phone = telephone.toLong()
            )
        _state.postValue(BaseState.UpdateUI(true, "Sending details to the server, Please wait..."))
        setEmployeeUseCase.invoke(viewModelScope, SetEmployeeUseCase.Input(entity!!, companyId.value!!)){

            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                handleFailure(failure)
            })
        }
    }

    fun deleteEmployee() {
        _state.postValue(BaseState.UpdateUI(true, "Sending details to the server, Please wait..."))
        deleteEmployeeUseCase.invoke(viewModelScope, DeleteEmployeeUseCase.Input(entity!!, companyId.value!!)){

            it.result(onSuccess = {
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                handleFailure(failure)
            })
        }
    }

}