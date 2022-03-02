package ke.co.shambapay.data.model

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
){
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

    fun getFullName(): String = "$firstName $lastName"
}
