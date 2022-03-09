package ke.co.shambapay.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.domain.Failures
import ke.co.shambapay.domain.UploadUseCase
import ke.co.shambapay.domain.base.BaseState
import org.joda.time.DateTime
import java.io.InputStream

class UploadViewModel(
    val uploadUseCase: UploadUseCase
) :ViewModel() {

    val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    val _month = MutableLiveData<Int>()
    val month: LiveData<Int> = _month

    val _year = MutableLiveData<Int>()
    val year: LiveData<Int> = _year

    val _inputStream = MutableLiveData<InputStream?>()
    val inputStream: LiveData<InputStream?> = _inputStream

    val _canUploadEmployees = MutableLiveData<Boolean>()
    val canUploadEmployees: LiveData<Boolean> = _canUploadEmployees

    val _canUploadWork = MutableLiveData<Boolean>()
    val canUploadWork: LiveData<Boolean> = _canUploadWork

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canUploadEmployees.postValue(true)
        _canUploadWork.postValue(false)
    }

    fun validateUploadWork (
        month: String?,
        year: String?
    ){
        _canUploadWork.postValue(false)

        val convertedMonth = month?.toIntOrNull()
        val convertedYear = year?.toIntOrNull()

        if (convertedMonth == null || convertedMonth <= 0 || convertedMonth > 12){
            _state.postValue(BaseState.UpdateUI(false, "Month is not valid"))
            return
        }

        if (convertedYear == null || convertedYear <= 0 || convertedYear > DateTime.now().year){
            _state.postValue(BaseState.UpdateUI(false, "Year is not valid"))
            return
        }

        if (convertedMonth > DateTime.now().monthOfYear && convertedYear > DateTime.now().year().get()){
            _state.postValue(BaseState.UpdateUI(false, "Cannot set records for the future"))
            return
        }

        _canUploadWork.postValue(true)
        _state.postValue(BaseState.UpdateUI(false, ""))

        _month.postValue(convertedMonth!!)
        _year.postValue(convertedYear!!)
    }

    fun setInputStream(inputStream: InputStream?){
        _inputStream.postValue(inputStream)
    }

    fun uploadWork() {

        _state.postValue(BaseState.UpdateUI(true, "Uploading work records, Please wait..."))
        uploadUseCase.invoke(viewModelScope, UploadUseCase.Input.Work(inputStream.value, month.value!!, year.value!!)){

            it.result(onSuccess = {
                _inputStream.postValue(null)
                _state.postValue(BaseState.UpdateUI(false, "Success"))
                _state.postValue(BaseState.Success(Unit))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(BaseState.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(BaseState.UpdateUI(false, "Unknown error, please check back later"))}
                }
            })
        }
    }

    fun uploadEmployees() {


        _state.postValue(BaseState.UpdateUI(true, "Uploading Employee records, Please wait..."))
        uploadUseCase.invoke(viewModelScope, UploadUseCase.Input.Employees(inputStream.value)){

            it.result(onSuccess = {
                _inputStream.postValue(null)
                _state.postValue(BaseState.UpdateUI(false, "Success"))

            }, onFailure = { failure ->
                when(failure){
                    is Failures.WithMessage -> {_state.postValue(BaseState.UpdateUI(false, failure.message))}

                    else ->{_state.postValue(BaseState.UpdateUI(false, "Unknown error, please check back later"))}
                }
            })
        }
    }

}