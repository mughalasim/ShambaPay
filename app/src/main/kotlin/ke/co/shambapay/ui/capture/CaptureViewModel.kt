package ke.co.shambapay.ui.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.*
import org.joda.time.DateTime

class CaptureViewModel(
//    val setEmployeeWorkUseCase: SetEmployeeWorkUseCase
): ViewModel() {

    val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    val _unit = MutableLiveData<String>()
    val unit: LiveData<String> = _unit

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
        _unit.postValue("")
        _rateId.postValue("")
    }

    fun validate(
        unit: String?,
        rateId: String?,
    ){
        _canSubmit.postValue(false)

        if (unit.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Units cannot be empty"))
            return
        }
        if (rateId.isNullOrEmpty()) {
            _state.postValue(State.UpdateUI(false, "Please select a rate"))
            return
        }

        _canSubmit.postValue(true)
        _state.postValue(State.UpdateUI(false, ""))

        _unit.postValue(unit!!)
        _rateId.postValue(rateId!!)
    }

    fun updateEmployeeWork(employeeID: String) {

        val workEntity = WorkEntity (
            date = DateTime.now().toString(),
            unit = unit.value!!.toDoubleOrNull(),
            rateId = rateId.value!!
        )

        _state.postValue(State.UpdateUI(true, "Setting up user, Please wait..."))
//        setEmployeeWorkUseCase.invoke(viewModelScope, employeeID, workEntity){
//
//            it.result(onSuccess = {
//                _state.postValue(State.Success)
//
//            }, onFailure = { failure ->
//                when(failure){
//                    is Failures.WithMessage -> {_state.postValue(
//                        State.UpdateUI(
//                            false,
//                            failure.message
//                        )
//                    )}
//
//                    else ->{_state.postValue(
//                        State.UpdateUI(
//                            false,
//                            "Unknown error when authenticating, please check back later"
//                        )
//                    )}
//                }
//            })
//        }
    }

}