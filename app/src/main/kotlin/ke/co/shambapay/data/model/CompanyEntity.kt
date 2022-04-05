package ke.co.shambapay.data.model

import java.util.*

data class CompanyEntity (
    val Employees: HashMap<String, EmployeeEntity>,
    val Settings: SettingsEntity,
    var isDefault: Boolean = false
){
    constructor(): this (
        Employees =  HashMap<String, EmployeeEntity>(),
        Settings = SettingsEntity(),
        isDefault = false
    )

    fun getNewCompany(companyName: String): CompanyEntity {
        val rateId = UUID.randomUUID().toString()
        val companyId = UUID.randomUUID().toString()
        return CompanyEntity (
            Employees = HashMap(),
            Settings = SettingsEntity(
                companyId = companyId,
                companyName = companyName,
                rates =  hashMapOf (rateId to JobRateEntity(
                    rateId = rateId,
                    measurement = "Weight",
                    jobType = JobType.PICKING,
                    rate = 1.0
                ))
            )
        )
    }
}