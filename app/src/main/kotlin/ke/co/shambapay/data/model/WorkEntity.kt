package ke.co.shambapay.data.model

import org.joda.time.DateTime

data class WorkEntity (
    val date: DateTime,
    val unit: Double,
    val rateId: String
){
    constructor(): this(
        date = DateTime(),
        unit = 0.0,
        rateId = ""
    )
}
