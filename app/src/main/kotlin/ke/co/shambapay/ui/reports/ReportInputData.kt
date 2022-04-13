package ke.co.shambapay.ui.reports

import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.ReportType
import org.joda.time.DateTime
import java.io.Serializable

data class ReportInputData (
    val startDate: DateTime,
    val endDate: DateTime,
    var employee: EmployeeEntity?,
    val reportType: ReportType
    ) : Serializable