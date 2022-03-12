package ke.co.shambapay.data.model

data class JobRateEntity (
    val jobType: JobType,
    val rate: Double
){
    constructor(): this(
        jobType = JobType.Picking,
        rate = 0.0
    )
}