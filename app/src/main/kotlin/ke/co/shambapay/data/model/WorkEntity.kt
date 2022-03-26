package ke.co.shambapay.data.model

data class WorkEntity (
    val dateString: String,
    val employeeId: String,
    val rateId: String,
    val unit: Double? = 0.0,
    val yearPlusMonth: Int,
    val employeeIdPlusMonth: String
){
    constructor(): this(
        dateString = "",
        employeeId = "",
        rateId = "",
        unit = 0.0,
        yearPlusMonth = 0,
        employeeIdPlusMonth = ""
    )
}
