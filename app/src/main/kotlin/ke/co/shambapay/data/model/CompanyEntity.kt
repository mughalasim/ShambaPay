package ke.co.shambapay.data.model

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
}