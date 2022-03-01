package ke.co.shambapay.data.model

data class UserEntity (
    val userId: String = "",
    val nationalId: String = "",
    val companyId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: Int = 0,
    val areaCode: Int = 0,
    val userType: UserType,
    val fcmToken: String = ""
){
    fun getFullName(): String = "$firstName $lastName"
}
