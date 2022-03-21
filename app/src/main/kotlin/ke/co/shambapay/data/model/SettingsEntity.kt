package ke.co.shambapay.data.model

data class SettingsEntity (
    val companyId: String = "",
    val companyName: String = "",
    val rates: HashMap<String, JobRateEntity>
){
    constructor(): this(
        companyId = "",
        companyName = "",
        rates = HashMap<String, JobRateEntity>()
    )
}
