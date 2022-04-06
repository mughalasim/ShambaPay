package ke.co.shambapay.data.model

import java.io.Serializable

data class UserEntity (
    var id: String = "",
    var companyId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    val email: String = "",
    var phone: Long = 0,
    val areaCode: Int = 0,
    val userType: UserType,
    val fcmToken: String = ""
): Serializable {

    constructor(): this(
        id = "",
        companyId = "",
        firstName = "",
        lastName = "",
        email = "",
        phone = 0,
        areaCode = 0,
        userType = UserType.ADMIN,
        fcmToken = ""
    )

    fun fetchFullName(): String = "$firstName $lastName"

    fun fetchPhoneNumber(): String = if (phone == 0L) "" else phone.toString()
}
