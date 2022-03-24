package ke.co.shambapay.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.domain.GetReportUseCase
import ke.co.shambapay.domain.base.BaseState
import org.joda.time.DateTime

class ReportViewModel(
    private val getReportUseCase: GetReportUseCase
) : ViewModel() {

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
    }


    fun validate (
        year: String?,
        month: String?
    ){
        _canSubmit.postValue(false)

        val yearUnit = year?.toIntOrNull()
        val monthUnit = month?.toIntOrNull()

        if (yearUnit == null || yearUnit <= 2000 || yearUnit > DateTime.now().year){
            _state.postValue(BaseState.UpdateUI(false, "Invalid year"))
            return
        }

        if (monthUnit == null || monthUnit <= 0 || monthUnit > 12){
            _state.postValue(BaseState.UpdateUI(false, "Invalid month"))
            return
        }

        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(true)

    }

    fun fetchReport(reportType: ReportType, year: Int, month: Int) {

    }

}