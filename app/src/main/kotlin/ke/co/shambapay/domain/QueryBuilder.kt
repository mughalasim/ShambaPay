package ke.co.shambapay.domain

import ke.co.shambapay.BuildConfig
import org.joda.time.DateTime

object QueryBuilder {

    fun getEmployees(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_EMPLOYEES}"
    }

    fun setWork(companyId: String, employeeId: String, date: DateTime): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}/${employeeId}-${date.year}-${date.monthOfYear}-${date.dayOfMonth}"
    }

    fun getWork(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}"
    }

    fun getSettings(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}"
    }

    fun getRates(companyId: String, rateId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_SETTINGS}/${BuildConfig.DB_REF_RATES}/${rateId}"
    }

    fun geUser(userId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_USERS}/${userId}"
    }


}