package ke.co.shambapay.data.model

import java.io.Serializable

data class JobRateEntity (
    val rateId: String,
    val measurement: String,
    val jobType: JobType,
    val rate: Double
): Serializable {
    constructor(): this(
        rateId = "",
        measurement = "",
        jobType = JobType.PICKING,
        rate = 0.0
    )
}