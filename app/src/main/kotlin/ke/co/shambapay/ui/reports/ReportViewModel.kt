package ke.co.shambapay.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.intent.ReportInputData
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.domain.GetReportUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel
import org.joda.time.DateTime

class ReportViewModel(
    private val getReportUseCase: GetReportUseCase
) : BaseViewModel() {

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    private var startDate: DateTime? = null
    private var endDate: DateTime? = null

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
        clearDates()
        validate()
    }

    sealed class ViewModelOutput {
        data class Report(val list: List<ReportEntity>) : ViewModelOutput()
    }

    fun setStartDate(date: DateTime){
        startDate = date
        validate()
    }

    fun setEndDate(date: DateTime){
        endDate = date
        validate()
    }

    fun clearDates(){
        startDate = null
        endDate = null
    }

    fun getReportInputData(position: Int): ReportInputData {
        return ReportInputData(
            startDate = startDate!!,
            endDate = endDate!!,
            employee = null,
            reportType = ReportType.values()[position]
        )
    }


    fun validate (){

        _canSubmit.postValue(false)

        if (startDate == null || endDate == null){
            _state.postValue(BaseState.UpdateUI(false, "Start date or End date are not set"))
            return
        }

        if (startDate!!.isAfter(endDate)){
            _state.postValue(BaseState.UpdateUI(false, "Start date cannot be after the end date"))
            return
        }

        if (startDate!!.isAfterNow){
            _state.postValue(BaseState.UpdateUI(false, "Start date cannot be in the future"))
            return
        }

        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(true)

    }

    fun fetchReport(reportInputData: ReportInputData) {
        _state.postValue(BaseState.UpdateUI(true, "Generating the report, Please wait...."))
        getReportUseCase.invoke(viewModelScope, reportInputData){
            it.result(
                onSuccess = { list ->
                    _state.postValue(BaseState.Success(ViewModelOutput.Report(list)))
                },
                onFailure = { failure ->
                    handleFailure(failure)
                }
            )
        }
    }

}