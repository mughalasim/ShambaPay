package ke.co.shambapay.ui.capture

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.SetEmployeeWorkUseCase
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel
import org.joda.time.DateTime

class CaptureViewModel(
    private val globalState: UiGlobalState,
    private val setEmployeeWorkUseCase: SetEmployeeWorkUseCase
): BaseViewModel() {

    private val _unit = MutableLiveData<Double>()
    val unit: LiveData<Double> = _unit

    private val _rateId = MutableLiveData<String>()
    val rateId: LiveData<String> = _rateId

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
        _unit.postValue(0.0)
        _rateId.postValue("")
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun validate(
        unit: String?,
        position: Int,
    ){
        _canSubmit.postValue(false)

        if (unit.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Please set Units"))
            return
        }

        val workUnit = unit.toDoubleOrNull()

        if (workUnit == null || workUnit <= 0) {
            _state.postValue(BaseState.UpdateUI(false, "Invalid units set"))
            return
        }
        if (position == 0) {
            _state.postValue(BaseState.UpdateUI(false, "Please select a rate"))
            return
        }

        val rateId = globalState.getRateIdForIndex(position-1)

        if (rateId.isEmpty()){
            _state.postValue(BaseState.UpdateUI(false, "Invalid rate Id"))
            return
        }

        _canSubmit.postValue(true)
        _state.postValue(BaseState.UpdateUI(false, ""))

        _unit.postValue(workUnit)
        _rateId.postValue(rateId)
    }

    fun updateEmployeeWork(employeeId: String) {

        val date = DateTime.now()
        val workEntity = WorkEntity (
            dateString = date.toString(),
            employeeId = employeeId,
            rateId = rateId.value!!,
            unit = unit.value!!
        )

        _state.postValue(BaseState.UpdateUI(true, "Capturing data, Please wait..."))
        setEmployeeWorkUseCase.invoke(viewModelScope, SetEmployeeWorkUseCase.Input.Details(workEntity, employeeId, date)){
            it.result(
                onSuccess = {
                    _state.postValue(BaseState.Success(Unit))
                },
                onFailure = { failure ->
                    handleFailure(failure)
                }
            )
        }
    }
}