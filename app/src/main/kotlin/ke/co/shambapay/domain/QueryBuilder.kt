package ke.co.shambapay.domain

import ke.co.shambapay.BuildConfig
import org.joda.time.DateTime

object QueryBuilder {

    fun getEmployees(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_EMPLOYEES}"
    }

    fun getWork(companyId: String, employeeId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}/${employeeId}"
    }

    fun setWork(companyId: String, employeeId: String): String {
        val date = DateTime.now()
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}/${employeeId}/${date.year}/${date.monthOfYear}/${date.dayOfMonth}"
    }

    fun getSettings(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}"
    }

    fun geUser(userId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_USERS}/${userId}"
    }


}