package ke.co.shambapay.data.model

data class WorkEntity (
    val date: Long,
    val employeeId: String,
    val rateId: String,
    val unit: Double? = 0.0,
    val total: Double? = 0.0,
){
    constructor(): this(
        date = 0L,
        employeeId = "",
        rateId = "",
        unit = 0.0,
        total = 0.0
    )
}
