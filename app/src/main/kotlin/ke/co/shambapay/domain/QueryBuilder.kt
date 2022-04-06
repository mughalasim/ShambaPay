package ke.co.shambapay.domain

import ke.co.shambapay.BuildConfig
import org.joda.time.DateTime

object QueryBuilder {

    fun getCompanies(): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}"
    }

    fun getCompany(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}"
    }

    fun getCompanyName(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}/companyName"
    }

    fun getUsers(): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_USERS}"
    }

    fun getEmployees(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_EMPLOYEES}"
    }

    fun getEmployee(companyId: String, employeeId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_EMPLOYEES}/$employeeId"
    }

    fun setWork(companyId: String, employeeId: String, date: DateTime): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_WORK}/${companyId}/${employeeId}-${date.year}-${date.monthOfYear}-${date.dayOfMonth}"
    }

    fun getWork(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_WORK}/${companyId}"
    }

    fun getSettings(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}"
    }

    fun getRates(companyId: String, rateId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}/${BuildConfig.DB_REF_RATES}/${rateId}"
    }

    fun getUser(userId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_USERS}/${userId}"
    }


}