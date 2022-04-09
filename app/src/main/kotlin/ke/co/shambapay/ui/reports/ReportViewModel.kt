package ke.co.shambapay.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.domain.GetEmployeesUseCase
import ke.co.shambapay.domain.GetReportUseCase
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel
import org.joda.time.DateTime

class ReportViewModel(
    private val getReportUseCase: GetReportUseCase,
    private val getEmployeesUseCase: GetEmployeesUseCase
) : BaseViewModel() {

    private val _canSubmit = MutableLiveData<Boolean>()
    val canSubmit: LiveData<Boolean> = _canSubmit

    private val _employees = MutableLiveData<List<EmployeeEntity>>()
    val employees: LiveData<List<EmployeeEntity>> = _employees

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
        _canSubmit.postValue(false)
    }

    sealed class ViewModelOutput {
        data class Report(val list: List<ReportEntity>) : ViewModelOutput()
        data class Employee(val list: List<String>) : ViewModelOutput()
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

    fun fetchReport(reportType: ReportType, date: DateTime, employee: EmployeeEntity?) {
        _state.postValue(BaseState.UpdateUI(true, "Generating the report, Please wait...."))
        getReportUseCase.invoke(viewModelScope, GetReportUseCase.Input(
                reportType = reportType,
                date = date,
                employee = employee
            )){
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

    fun getEmployee (index: Int): EmployeeEntity? {
        return employees.value?.get(index)
    }

    fun fetchEmployees() {
        _state.postValue(BaseState.UpdateUI(true, "Fetching all employees, Please wait...."))
        getEmployeesUseCase.invoke(viewModelScope, GetEmployeesUseCase.Input(filter = "")){
            it.result(
                onSuccess = { list ->
                    _employees.postValue(list)
                    val data = list.map { entity ->
                        entity.fetchFullName()
                    }
                    _state.postValue(BaseState.Success(ViewModelOutput.Employee(data)))
                },
                onFailure = { failure ->
                    handleFailure(failure)
                }
            )
        }
    }

}