package ke.co.shambapay.data.model

import java.io.Serializable

data class UserEntity (
    var id: String = "",
    val companyId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: Long = 0,
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
}
