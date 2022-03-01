package ke.co.shambapay.data.model

data class EmployeeEntity (
    val nationalId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val work: List<WorkEntity> = emptyList(),
    val cash: Double = 0.0,
    val advance: Double = 0.0,
    val misc: Double = 0.0,
    val nhif: Int = 0,
    val nssf: Int = 0,
    val phone: Int = 0,
    val areaCode: Int = 0
){
    fun getFullName(): String = "$firstName $lastName"
}
