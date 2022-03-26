package ke.co.shambapay.data.model

data class ReportEntity (
    val item: String,
    val unit: Double,
    val isHeading: Boolean = false
){
    constructor(): this(
        item = "",
        unit = 0.0,
        isHeading = false
    )
}