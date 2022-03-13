package ke.co.shambapay.ui.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.*
import ke.co.shambapay.ui.UiGlobalState
import org.joda.time.DateTime

class CaptureViewModel(
    val globalState: UiGlobalState,
    val setEmployeeWorkUseCase: SetEmployeeWorkUseCase
): ViewModel() {

    val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    val _unit = MutableLiveData<Double>()
    val unit: LiveData<Double> = _unit

    val _rateId = MutableLiveData<String>()
    val rateId: LiveData<String> = _rateId

    val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    sealed class State {
        data class UpdateUI(val showLoading: Boolean, val message: String): State()
        object Success: State()
    }

    init {
        _state.postValue(State.UpdateUI(false, ""))
        _canSubmit.postValue(false)
        _unit.postValue(0.0)
        _rateId.postValue("")
    }

    fun validate(
        unit: String?,
        position: Int,
    ){
        _canSubmit.postValue(false)

        if (unit.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Please set Units"))
            return
        }

        val workUnit = unit.toDoubleOrNull()

        if (workUnit == null || workUnit <= 0) {
            _state.postValue(State.UpdateUI(false, "Invalid units set"))
            return
        }
        if (position == 0) {
            _state.postValue(State.UpdateUI(false, "Please select a rate"))
            return
        }

        val rateId = globalState.getRateIdForIndex(position-1)

        if (rateId.isEmpty()){
            _state.postValue(State.UpdateUI(false, "Invalid rate Id"))
            return
        }

        _canSubmit.postValue(true)
        _state.postValue(State.UpdateUI(false, ""))

        _unit.postValue(workUnit!!)
        _rateId.postValue(rateId)
    }

    fun updateEmployeeWork(employeeID: String) {

        val workEntity = WorkEntity (
            date = DateTime.now().toString(),
            unit = unit.value!!,
            rateId = rateId.value!!
        )

        _state.postValue(State.UpdateUI(true, "Capturing data, Please wait..."))
        setEmployeeWorkUseCase.invoke(viewModelScope, SetEmployeeWorkUseCase.Input.Details(workEntity, employeeID)){
            it.result(
                onSuccess = {
                    _state.postValue(State.Success)
                },
                onFailure = { failure ->
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
                }
            )
        }
    }
}