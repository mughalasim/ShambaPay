package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.*
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.ui.UiGlobalState
import kotlinx.coroutines.CompletableDeferred
import org.joda.time.DateTime
import java.math.RoundingMode

class GetReportUseCase(val globalState: UiGlobalState): BaseUseCase<GetReportUseCase.Input, List<ReportEntity>, Failures>() {

    data class Input(val reportType: ReportType, val date: DateTime, val employeeEntity: EmployeeEntity?)

    override suspend fun run(input: Input): BaseResult<List<ReportEntity>, Failures> {

        if (globalState.user == null) return BaseResult.Failure(Failures.NotAuthenticated)

        if (globalState.user!!.userType == UserType.SUPERVISOR && input.reportType != ReportType.EMPLOYEE_PERFORMANCE){
            return BaseResult.Failure(Failures.WithMessage("You are not authorised to view this Report"))
        }

        val result = getAllWork(input)
        if (result is BaseResult.Failure) return result

        val allWork = (result as BaseResult.Success).successType

        when(input.reportType){
            ReportType.PAYROLL_SUMMARY -> {
                return BaseResult.Success(getPayrollSummary(allWork))
            }

            ReportType.EMPLOYEE_PERFORMANCE -> {
                if (input.employeeEntity == null) return BaseResult.Failure(Failures.WithMessage("Employee not selected"))
                return BaseResult.Success(getEmployeePerformanceSummary(allWork, input.employeeEntity))
            }

            ReportType.BANK_PAYMENT_DETAILS -> {
                return BaseResult.Success(getPayrollSummary(allWork))
            }

            ReportType.PAYSLIP -> {
                if (input.employeeEntity == null) return BaseResult.Failure(Failures.WithMessage("Employee not selected"))
                return BaseResult.Success(getPayrollSummary(allWork))
            }
        }
    }


    private suspend fun getAllWork(input: Input): BaseResult<List<WorkEntity>, Failures> {

        val def = CompletableDeferred<BaseResult<List<WorkEntity>, Failures>>()

        FirebaseDatabase.getInstance().getReference(QueryBuilder.getWork(globalState.user!!.companyId))
            .orderByChild("yearPlusMonth").equalTo((input.date.year+input.date.monthOfYear).toDouble()).get().
            addOnSuccessListener{ dataSnapshot ->
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
                def.complete(BaseResult.Failure(Failures.WithMessage("There was an issue with one of the data sets: " + e.localizedMessage)))
            }
        }.addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage ?: "")))
        }

        return def.await()
    }

    private fun getPayrollSummary(allWork: List<WorkEntity>): List<ReportEntity> {
        var grandTotal = 0.0
        val adminFees = 5000.0
        grandTotal += adminFees

        val responseList: MutableList<ReportEntity> = mutableListOf()

        globalState.settings?.rates?.forEach { (rateId, jobRateEntity) ->
            val work = allWork.filter { it.rateId == rateId }

            var totalUnits = 0.0

            work.forEach {
                totalUnits += it.unit ?: 0.0
            }

            val totalCost = globalState.getTotalForRateIdAndUnit(rateId, totalUnits)
            grandTotal += totalCost

            responseList.add(ReportEntity("${jobRateEntity.jobType.name.lowercase()} measured in ${jobRateEntity.measurement.lowercase()}", totalUnits))
            responseList.add(ReportEntity("Cost for ${jobRateEntity.jobType.name} (rate: ${jobRateEntity.rate})", totalCost, isHeading = true))
        }

        responseList.add(ReportEntity("Cost for administration and licensing", adminFees))

        responseList.add(ReportEntity(item = "Grand Total", unit = grandTotal, isHeading = true))

        return responseList
    }

    private fun getEmployeePerformanceSummary(allWork: List<WorkEntity>, employeeEntity: EmployeeEntity): List<ReportEntity> {
        var grandTotal = 0.0

        val responseList: MutableList<ReportEntity> = mutableListOf()

        val employeesWork = allWork.filter { it.employeeId ==  employeeEntity.id}

        responseList.add(ReportEntity("Employee: ${employeeEntity.getFullName()}", 0.0, isHeading = true))
        responseList.add(ReportEntity("Phone: ${employeeEntity.phone}", 0.0, isHeading = true))

        globalState.settings?.rates?.forEach { (rateId, jobRateEntity) ->
            val work = employeesWork.filter { it.rateId == rateId }

            var totalUnits = 0.0

            if (work.isNotEmpty()){
                responseList.add(ReportEntity("All work done for ${jobRateEntity.jobType.name.lowercase()}", 0.0, isHeading = true))

                work.forEach {
                    totalUnits += it.unit ?: 0.0
                    responseList.add(ReportEntity(DateTime.parse(it.dateString).toString("EEEE dd/MMM/yyyy"), it.unit ?: 0.0))
                }

                val totalCost = globalState.getTotalForRateIdAndUnit(rateId, totalUnits)
                grandTotal += totalCost

                responseList.add(ReportEntity("${jobRateEntity.jobType.name.lowercase()} measured in ${jobRateEntity.measurement.lowercase()}", totalUnits))
                responseList.add(ReportEntity("Cost for ${jobRateEntity.jobType.name} (rate: ${jobRateEntity.rate})", totalCost))
            }
        }

        responseList.add(ReportEntity(item = if(grandTotal != 0.0) "Grand Total" else "No work done for the selected month", unit = grandTotal, isHeading = grandTotal != 0.0))

        return responseList
    }

}