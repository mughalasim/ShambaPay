package ke.co.shambapay.data.model

data class ReportEntity (
    val item: String,
    val unit: Double
){
    constructor(): this(
        item = "",
        unit = 0.0
    )
}