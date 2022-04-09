package ke.co.shambapay.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.*
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.domain.utils.Failures
import ke.co.shambapay.domain.utils.QueryBuilder
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.utils.TaxFormula
import kotlinx.coroutines.CompletableDeferred
import org.joda.time.DateTime

class GetReportUseCase(
    private val globalState: UiGlobalState,
    private val getEmployeesUseCase: GetEmployeesUseCase
    ): BaseUseCase<GetReportUseCase.Input, List<ReportEntity>, Failures>() {

    data class Input(val reportType: ReportType, val date: DateTime, val employee: EmployeeEntity?)

    override suspend fun run(input: Input): BaseResult<List<ReportEntity>, Failures> {

        FirebaseAuth.getInstance().currentUser ?: return BaseResult.Failure(Failures.NotAuthenticated)

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
                if (input.employee == null) return BaseResult.Failure(Failures.WithMessage("Employee not selected"))
                return BaseResult.Success(getEmployeePerformanceSummary(allWork, input.employee))
            }

            ReportType.BANK_PAYMENT_DETAILS -> {
                val employeeResult = getEmployeesUseCase.run(GetEmployeesUseCase.Input(globalState.settings!!.companyId, ""))
                if(employeeResult is BaseResult.Failure) return  employeeResult
                val employees = (employeeResult as BaseResult.Success).successType
                if(employees.isNullOrEmpty()) return BaseResult.Failure(Failures.WithMessage("Failed to fetch all employees"))
                return BaseResult.Success(getBankPaymentsToAllEmployees(allWork, employees))
            }

            ReportType.PAYSLIP -> {
                if (input.employee == null) return BaseResult.Failure(Failures.WithMessage("Employee not selected"))
                return BaseResult.Success(getEmployeePaySlip(allWork, input.employee))
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

        responseList.add(ReportEntity("Employee: ${employeeEntity.fetchFullName()}", 0.0, isHeading = true))
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

    private fun getEmployeePaySlip(allWork: List<WorkEntity>, employeeEntity: EmployeeEntity): List<ReportEntity> {
        var grossPay = 0.0

        val responseList: MutableList<ReportEntity> = mutableListOf()

        val employeesWork = allWork.filter { it.employeeId ==  employeeEntity.id}

        responseList.add(ReportEntity("Employee: ${employeeEntity.fetchFullName()}", 0.0, isHeading = true))
        responseList.add(ReportEntity("Phone: ${employeeEntity.phone}", 0.0, isHeading = true))

        globalState.settings?.rates?.forEach { (rateId, jobRateEntity) ->
            val work = employeesWork.filter { it.rateId == rateId }

            var totalUnits = 0.0

            if (work.isNotEmpty()){
                responseList.add(ReportEntity("Income for ${jobRateEntity.jobType.name.lowercase()}", 0.0, isHeading = true))

                work.forEach {
                    totalUnits += it.unit ?: 0.0
                }

                val totalCost = globalState.getTotalForRateIdAndUnit(rateId, totalUnits)
                grossPay += totalCost

                responseList.add(ReportEntity("${String.format("%,.2f", totalUnits).trim()} ${jobRateEntity.measurement.lowercase()} @ rate of ${jobRateEntity.rate}", totalCost))
            }
        }

        if (grossPay == 0.0){
            responseList.add(ReportEntity(item = "No work done for the selected month", unit = grossPay, isHeading = false))
        } else {

            responseList.add(ReportEntity(item = "Gross income", unit = grossPay, isHeading = true))

            responseList.add(ReportEntity(item = "NSSF", unit = TaxFormula().getNssfRate()))

            val taxable = grossPay - TaxFormula().getNssfRate()
            responseList.add(ReportEntity(item = "Taxable pay", unit = taxable))

            val payee = TaxFormula().getPayForGrossIncome(taxable)
            responseList.add(ReportEntity(item = "PAYEE", unit = payee))

            val personalRelief = TaxFormula().getPersonalRelief(payee)
            responseList.add(ReportEntity(item = "Personal relief", unit = personalRelief))

            val nhif = TaxFormula().getNhifForGrossIncome(taxable)
            responseList.add(ReportEntity(item = "Nhif", unit = nhif))

            val taxPayable = if (payee == 0.0 || payee < personalRelief) 0.0 else (payee - personalRelief)
            responseList.add(ReportEntity(item = "Tax Payable", unit = taxPayable))

            val netPay = grossPay - (taxPayable + nhif + TaxFormula().getNssfRate())
            responseList.add(ReportEntity(item = "Net income", unit = netPay, isHeading = true))

        }

        return responseList
    }

    private fun getBankPaymentsToAllEmployees(allWork: List<WorkEntity>, employees: List<EmployeeEntity>): List<ReportEntity> {
        var grandTotal = 0.0

        val responseList: MutableList<ReportEntity> = mutableListOf()

        employees.forEach {  employee ->
            val allEmployeesWork = allWork.filter { it.employeeId == employee.id }
            var employeesTotal = 0.0

            allEmployeesWork.forEach { work ->
                employeesTotal += globalState.getTotalForRateIdAndUnit( work.rateId, work.unit)
            }

            grandTotal += employeesTotal

            if (employeesTotal != 0.0) responseList.add(ReportEntity("${employee.fetchFullName()} - ${employee.phone}", employeesTotal))
        }

        responseList.add(ReportEntity(item = if(grandTotal != 0.0) "Grand Total" else "No work done for the selected month", unit = grandTotal, isHeading = grandTotal != 0.0))

        return responseList
    }

}