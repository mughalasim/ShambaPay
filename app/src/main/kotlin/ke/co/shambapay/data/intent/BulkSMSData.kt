package ke.co.shambapay.data.intent

data class BulkSMSData(
    val fullName: String,
    val phone: Long,
    val amount: Double
)
