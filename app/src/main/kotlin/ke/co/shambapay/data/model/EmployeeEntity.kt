package ke.co.shambapay.data.model

data class EmployeeEntity (
    val id: Int = 0,
    val nationalId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val cash: Double = 0.0,
    val advance: Double = 0.0,
    val misc: Double = 0.0,
    val nhif: String = "",
    val nssf: String = "",
    val phone: Int = 0,
    val areaCode: Int = 0
){

    constructor(): this(
        id = 0,
        nationalId = 0,
        firstName = "",
        lastName = "",
        cash = 0.0,
        advance = 0.0,
        misc = 0.0,
        nhif = "",
        nssf = "",
        phone = 0,
        areaCode = 0
    )

    fun getFullName(): String = "$firstName $lastName"
}
