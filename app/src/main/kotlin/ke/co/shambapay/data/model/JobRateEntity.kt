package ke.co.shambapay.data.model

data class JobRateEntity (
    val rateId: String,
    val measurement: String,
    val jobType: JobType,
    val rate: Double
){
    constructor(): this(
        rateId = "",
        measurement = "",
        jobType = JobType.PICKING,
        rate = 0.0
    )
}