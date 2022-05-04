package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.intent.ReportInputData
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.data.model.ReportType
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.GenerateReport
import ke.co.shambapay.domain.utils.Query
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred
import org.joda.time.Months

class GetReportUseCase(
    private val globalState: UiGlobalState,
    private val getEmployeesUseCase: GetEmployeesUseCase
    ): BaseUseCase<ReportInputData, List<ReportEntity>, Failures>() {


    override suspend fun run(input: ReportInputData): BaseResult<List<ReportEntity>, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

        if (globalState.user!!.userType == UserType.SUPERVISOR && input.reportType != ReportType.EMPLOYEE_PERFORMANCE){
            return BaseResult.Failure(Failures.WithMessage("You are not authorised to view this Report"))
        }

        val result = getAllWork(input)
        if (result is BaseResult.Failure) return result

        val allWork = (result as BaseResult.Success).successType

        when(input.reportType){
            ReportType.PAYROLL_SUMMARY -> {
                return BaseResult.Success(GenerateReport().getPayrollSummary(allWork))
            }

            ReportType.EMPLOYEE_PERFORMANCE -> {
                if (input.employee == null) return BaseResult.Failure(Failures.WithMessage("Employee not selected"))
                return BaseResult.Success(GenerateReport().getEmployeePerformanceSummary(allWork, input.employee!!))
            }

            ReportType.BANK_PAYMENT_DETAILS -> {
                val employeeResult = getEmployeesUseCase.run(GetEmployeesUseCase.Input(globalState.settings!!.companyId, ""))
                if(employeeResult is BaseResult.Failure) return  employeeResult
                val employees = (employeeResult as BaseResult.Success).successType
                if(employees.isNullOrEmpty()) return BaseResult.Failure(Failures.WithMessage("No employees available to generate report"))
                return BaseResult.Success(GenerateReport().getBankPaymentsToAllEmployees(allWork, employees))
            }

            ReportType.PAYSLIP -> {
                if (input.employee == null) return BaseResult.Failure(Failures.WithMessage("Employee not selected"))
                return BaseResult.Success(GenerateReport().getEmployeePaySlip(allWork, input.employee!!))
            }
        }
    }

    private suspend fun getAllWork(input: ReportInputData): BaseResult<List<WorkEntity>, Failures> {


        if (Months.monthsBetween(input.startDate, input.endDate).months > 2)
            return BaseResult.Failure(Failures.WithMessage("Kindly narrow your selected date range within two months"))

        val def = CompletableDeferred<BaseResult<List<WorkEntity>, Failures>>()

        FirebaseDatabase.getInstance().getReference(Query.getWork(globalState.user!!.companyId))
            .orderByChild("dateString")
            .startAt(
                input.startDate.toString()
            ).endAt(
                input.endDate.toString()
            ).get().addOnSuccessListener{ dataSnapshot ->
            try {
                if (!dataSnapshot.hasChildren()){
                    def.complete(BaseResult.Success(emptyList()))
                } else {
                    val list = dataSnapshot.children.map { data ->
                        data.getValue(WorkEntity::class.java)!!
                    }
                    def.complete(BaseResult.Success(list))
                }
            } catch (e: Exception){
                def.complete(BaseResult.Failure(Failures.WithMessage(e.localizedMessage)))
            }
        }.addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }

        return def.await()
    }

}