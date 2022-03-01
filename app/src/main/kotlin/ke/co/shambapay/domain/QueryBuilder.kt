package ke.co.shambapay.domain

import ke.co.shambapay.BuildConfig

object QueryBuilder {

    fun getEmployees(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_EMPLOYEES}"
    }

    fun getWork(companyId: String, employeeId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}/${employeeId}"
    }

    fun getSettings(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}"
    }

    fun geUser(userId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_USERS}/${userId}"
    }


}