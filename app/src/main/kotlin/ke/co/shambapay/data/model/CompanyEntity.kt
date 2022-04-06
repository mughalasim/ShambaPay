package ke.co.shambapay.data.model

import java.util.*

data class CompanyEntity (
    val employees: HashMap<String, EmployeeEntity>,
    val settings: SettingsEntity,
    var fetchDefault: Boolean? = null
){
    constructor(): this (
        employees =  HashMap<String, EmployeeEntity>(),
        settings = SettingsEntity()
    )

    fun getNewCompany(companyName: String): CompanyEntity {
        val rateId = UUID.randomUUID().toString()
        val companyId = UUID.randomUUID().toString()
        return CompanyEntity (
            employees = HashMap(),
            settings = SettingsEntity(
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