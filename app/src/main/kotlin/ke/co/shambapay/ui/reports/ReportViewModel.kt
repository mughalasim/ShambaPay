package ke.co.shambapay.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ke.co.shambapay.domain.base.BaseState

class ReportViewModel: ViewModel() {

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
    }


    fun validate(
        reportType: Int,
        year: String?,
        month: String?
    ){
        _canSubmit.postValue(false)



//        _state.postValue(BaseState.UpdateUI(false, ""))
//        _canSubmit.postValue(true)
    }

}