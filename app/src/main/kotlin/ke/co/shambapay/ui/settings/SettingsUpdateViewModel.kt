package ke.co.shambapay.ui.settings

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.JobRateEntity
import ke.co.shambapay.data.model.JobType
import ke.co.shambapay.domain.DeleteRateUseCase
import ke.co.shambapay.domain.SetRateUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel
import java.util.*

class SettingsUpdateViewModel(
    private val setRateUseCase: SetRateUseCase,
    private val deleteRateUseCase: DeleteRateUseCase
) : BaseViewModel() {

    private val _measurement = MutableLiveData<String>()
    private val measurement: LiveData<String> = _measurement

    private val _jobTypePosition = MutableLiveData<Int>()
    private val jobTypePosition: LiveData<Int> = _jobTypePosition

    private val _rate = MutableLiveData<Double>()
    private val rate: LiveData<Double> = _rate

    private val _rateId = MutableLiveData<String>()
    val rateId: LiveData<String> = _rateId

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
        _rate.postValue(0.0)
        _measurement.postValue("")
        _rateId.postValue("")
    }

    fun setRateId(rateId: String?){
        _rateId.postValue(rateId ?: "")
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun validate(
        position: Int,
        measurement: String?,
        rate: String?
    ){
        _canSubmit.postValue(false)

        if (rate.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Please set a rate"))
            return
        }

        val rateUnit = rate.toDoubleOrNull()

        if (rateUnit == null || rateUnit <= 0) {
            _state.postValue(BaseState.UpdateUI(false, "Invalid rate set"))
            return
        }

        if (measurement.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "Please set a valid measurement"))
            return
        }

        _measurement.postValue(measurement)
        _jobTypePosition.postValue(position)
        _rate.postValue(rateUnit)

        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(true)
    }

    fun setJobRate() {
        val entity = JobRateEntity(
            rateId = rateId.value!!.ifEmpty { UUID.randomUUID().toString() },
            measurement = measurement.value!!,
            jobType = JobType.values()[jobTypePosition.value!!],
            rate = rate.value!!
        )

        _state.postValue(BaseState.UpdateUI(true, "Capturing data, Please wait..."))
        setRateUseCase.invoke(viewModelScope, entity) {
            it.result(
                onSuccess = { _state.postValue(BaseState.Success(Unit)) },

                onFailure = { failure ->
                    handleFailure(failure)
                }
            )
        }
    }

    fun deleteJobRate() {
        _state.postValue(BaseState.UpdateUI(true, "Deleting rate, Please wait..."))
        deleteRateUseCase.invoke(viewModelScope, rateId.value!!) {
            it.result(
                onSuccess = { _state.postValue(BaseState.Success(Unit)) },

                onFailure = { failure ->
                    handleFailure(failure)
                }
            )
        }
    }


}