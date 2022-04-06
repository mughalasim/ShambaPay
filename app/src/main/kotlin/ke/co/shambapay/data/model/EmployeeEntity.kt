package ke.co.shambapay.data.model

import java.io.Serializable

data class EmployeeEntity (
    val id: String = "",
    val nationalId: Long? = 0,
    val firstName: String = "",
    val lastName: String = "",
    val cash: Double = 0.0,
    val advance: Double = 0.0,
    val misc: Double = 0.0,
    val nhif: String = "",
    val nssf: String = "",
    val phone: Long = 0,
    val areaCode: Int = 0
): Serializable {

    constructor(): this(
        id = "",
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

    fun fetchFullName(): String = "$firstName $lastName"

    fun fetchFullNameUnderScore(): String = "${firstName.trim()}_${lastName.trim()}"

    fun fetchNationalId(): String = if (nationalId == 0L) "Not set" else nationalId.toString()

    fun fetchNationalIdEmpty(): String = if (nationalId == 0L) "" else nationalId.toString()

    fun fetchPhoneNumber(): String = if (phone == 0L) "Not set" else phone.toString()
}
