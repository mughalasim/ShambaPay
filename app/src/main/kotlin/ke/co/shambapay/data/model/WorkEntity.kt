package ke.co.shambapay.data.model

data class WorkEntity (
    val dateString: String,
    val employeeId: String,
    val rateId: String,
    val unit: Double? = 0.0
){
    constructor(): this(
        dateString = "",
        employeeId = "",
        rateId = "",
        unit = 0.0
    )
}
