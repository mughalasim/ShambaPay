package ke.co.shambapay.data.model

data class SettingsEntity (
    val companyId: String = "",
    val companyName: String = "",
    val rates: List<JobRateEntity>
){
    constructor(): this(
        companyId = "",
        companyName = "",
        rates = emptyList()
    )
}
