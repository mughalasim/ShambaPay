package ke.co.shambapay.data.model

import org.joda.time.DateTime

data class WorkEntity (
    val date: DateTime,
    val employeeId: String,
    val rateId: String,
    val unit: Double? = 0.0,
    val total: Double? = 0.0,
){
    constructor(): this(
        date = DateTime.now(),
        employeeId = "",
        rateId = "",
        unit = 0.0,
        total = 0.0
    )
}
