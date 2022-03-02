package ke.co.shambapay.data.model

data class JobRateEntity (
    val job_type: JobType,
    val rate: Double
){
    constructor(): this(
        job_type = JobType.Picking,
        rate = 0.0
    )
}