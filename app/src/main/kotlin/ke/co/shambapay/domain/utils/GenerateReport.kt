package ke.co.shambapay.domain.utils

import ke.co.shambapay.data.intent.BulkSMSData
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.ReportEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.utils.TaxFormula
import org.joda.time.DateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GenerateReport: KoinComponent {

    private val globalState: UiGlobalState by inject()
    private val txtNoWork = "No work done for the selected time period"
    private val txtGrandTotal = "Grand Total"
    private val adminFees = 500.0

    fun getPayrollSummary(
        allWork: List<WorkEntity>
    ): List<ReportEntity> {
        var grandTotal = 0.0
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

            responseList.add(
                ReportEntity(
                    item = "Total ${jobRateEntity.jobType.name.lowercase()} measured in ${jobRateEntity.measurement.lowercase()}",
                    unit = totalUnits
                )
            )
            responseList.add(
                ReportEntity(
                    item = "Cost for ${jobRateEntity.jobType.name.lowercase()} (rate: ${jobRateEntity.rate})",
                    unit = totalCost,
                    isHeading = true
                )
            )
        }

        responseList.add(
            ReportEntity(
                item = "Cost for administration and licensing",
                unit = adminFees
            )
        )

        responseList.add(
            ReportEntity(
                item = txtGrandTotal,
                unit = grandTotal,
                isHeading = true
            )
        )

        return responseList
    }

    fun getEmployeePerformanceSummary(
        allWork: List<WorkEntity>,
        employeeEntity: EmployeeEntity
    ): List<ReportEntity> {
        var grandTotal = 0.0

        val responseList: MutableList<ReportEntity> = mutableListOf()

        val employeesWork = allWork.filter { it.employeeId == employeeEntity.id }

        responseList.add(
            ReportEntity(
                item = "Employee: ${employeeEntity.fetchFullName()}",
                unit = 0.0,
                isHeading = false,
                showValue = false
            )
        )
        responseList.add(
            ReportEntity(
                item = "Phone: ${employeeEntity.phone}",
                unit = 0.0,
                isHeading = false,
                showValue = false
            )
        )

        globalState.settings?.rates?.forEach { (rateId, jobRateEntity) ->
            val work = employeesWork.filter { it.rateId == rateId }

            var totalUnits = 0.0

            if (work.isNotEmpty()) {
                responseList.add(
                    ReportEntity(
                        item = "All work done for ${jobRateEntity.jobType.name.lowercase()}",
                        unit = 0.0,
                        isHeading = true,
                        showValue = false
                    )
                )

                work.forEach {
                    totalUnits += it.unit ?: 0.0
                    responseList.add(
                        ReportEntity(
                            item = DateTime.parse(it.dateString).toString("EEEE dd/MMM/yyyy"),
                            unit = it.unit ?: 0.0
                        )
                    )
                }

                val totalCost = globalState.getTotalForRateIdAndUnit(rateId, totalUnits)
                grandTotal += totalCost

                responseList.add(
                    ReportEntity(
                        item = "Total ${jobRateEntity.jobType.name.lowercase()} measured in ${jobRateEntity.measurement.lowercase()}",
                        unit = totalUnits
                    )
                )
                responseList.add(
                    ReportEntity(
                        item = "Cost for ${jobRateEntity.jobType.name.lowercase()} (rate: ${jobRateEntity.rate})",
                        unit = totalCost
                    )
                )
            }
        }

        responseList.add(
            ReportEntity(
                item = if (grandTotal != 0.0) txtGrandTotal else txtNoWork,
                unit = grandTotal,
                isHeading = grandTotal != 0.0,
                showValue = grandTotal != 0.0
            )
        )

        return responseList
    }

    fun getEmployeePaySlip(
        allWork: List<WorkEntity>,
        employeeEntity: EmployeeEntity
    ): List<ReportEntity> {
        var grossPay = 0.0

        val responseList: MutableList<ReportEntity> = mutableListOf()

        val employeesWork = allWork.filter { it.employeeId == employeeEntity.id }

        responseList.add(
            ReportEntity(
                item = "Employee: ${employeeEntity.fetchFullName()}",
                unit = 0.0,
                isHeading = true
            )
        )
        responseList.add(
            ReportEntity(
                item = "Phone: ${employeeEntity.phone}",
                unit = 0.0,
                isHeading = true
            )
        )

        globalState.settings?.rates?.forEach { (rateId, jobRateEntity) ->
            val work = employeesWork.filter { it.rateId == rateId }

            var totalUnits = 0.0

            if (work.isNotEmpty()) {
                responseList.add(
                    ReportEntity(
                        item = "Income for ${jobRateEntity.jobType.name.lowercase()}",
                        unit = 0.0, isHeading = true
                    )
                )

                work.forEach {
                    totalUnits += it.unit ?: 0.0
                }

                val totalCost = globalState.getTotalForRateIdAndUnit(rateId, totalUnits)
                grossPay += totalCost

                responseList.add(
                    ReportEntity(
                        item = "${
                            String.format("%,.2f", totalUnits).trim()
                        } ${jobRateEntity.measurement.lowercase()} @ rate of ${jobRateEntity.rate}",
                        unit = totalCost
                    )
                )
            }
        }

        if (grossPay == 0.0) {
            responseList.add(
                ReportEntity(
                    item = txtNoWork,
                    unit = grossPay,
                    isHeading = false,
                    showValue = false
                )
            )
        } else {

            responseList.add(
                ReportEntity(
                    item = "Gross income",
                    unit = grossPay,
                    isHeading = true
                )
            )

            responseList.add(
                ReportEntity(
                    item = "NSSF",
                    unit = TaxFormula().getNssfRate()
                )
            )

            val taxable = grossPay - TaxFormula().getNssfRate()
            responseList.add(
                ReportEntity(
                    item = "Taxable pay",
                    unit = taxable
                )
            )

            val payee = TaxFormula().getPayForGrossIncome(taxable)
            responseList.add(
                ReportEntity(
                    item = "PAYEE",
                    unit = payee
                )
            )

            val personalRelief = TaxFormula().getPersonalRelief(payee)
            responseList.add(
                ReportEntity(
                    item = "Personal relief",
                    unit = personalRelief
                )
            )

            val nhif = TaxFormula().getNhifForGrossIncome(taxable)
            responseList.add(
                ReportEntity(
                    item = "Nhif",
                    unit = nhif
                )
            )

            val taxPayable =
                if (payee == 0.0 || payee < personalRelief) 0.0 else (payee - personalRelief)
            responseList.add(
                ReportEntity(
                    item = "Tax Payable",
                    unit = taxPayable
                )
            )

            val netPay = grossPay - (taxPayable + nhif + TaxFormula().getNssfRate())
            responseList.add(
                ReportEntity(
                    item = "Net income",
                    unit = netPay,
                    isHeading = true
                )
            )

        }

        return responseList
    }

    fun getBankPaymentsToAllEmployees(
        allWork: List<WorkEntity>,
        employees: List<EmployeeEntity>
    ): List<ReportEntity> {
        var grandTotal = 0.0

        val responseList: MutableList<ReportEntity> = mutableListOf()

        val smsData = mutableListOf<BulkSMSData>()
        employees.forEach { employee ->
            val allEmployeesWork = allWork.filter { it.employeeId == employee.id }
            var employeesTotal = 0.0

            allEmployeesWork.forEach { work ->
                employeesTotal += globalState.getTotalForRateIdAndUnit(work.rateId, work.unit)
            }

            grandTotal += employeesTotal
            if (employeesTotal != 0.0) {
                smsData.add (BulkSMSData (
                    fullName = employee.fetchFullName(),
                    phone = employee.phone,
                    amount = employeesTotal
                ))
                responseList.add (ReportEntity (
                    item = "${employee.fetchFullName()} - ${employee.phone}",
                    unit = employeesTotal
                ))
            }
        }

        globalState.bulkSMS = smsData

        responseList.add (
            ReportEntity (
                item = if (grandTotal != 0.0) txtGrandTotal else txtNoWork,
                unit = grandTotal,
                isHeading = grandTotal != 0.0,
                showValue = grandTotal != 0.0
            )
        )

        return responseList
    }
}