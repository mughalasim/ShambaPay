package ke.co.shambapay.data.model

import org.joda.time.DateTime

data class WorkEntity (
    val date: String,
    val unit: Double? = 0.0,
    val rateId: String
){
    constructor(): this(
        date = "",
        unit = 0.0,
        rateId = ""
    )
}
