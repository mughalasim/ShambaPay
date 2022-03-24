package ke.co.shambapay.domain

import ke.co.shambapay.BuildConfig
import org.joda.time.DateTime

object QueryBuilder {

    fun getEmployees(companyId: String): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_EMPLOYEES}"
    }

    fun getWork(companyId: String, employeeId: String, year: Int, month: Int): String {
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}/${employeeId}/${year}/${month}"
    }

    fun setWork(companyId: String, employeeId: String): String {
        val date = DateTime.now()
        return "${BuildConfig.DB_REF_ROOT}/${BuildConfig.DB_REF_COMPANIES}/${companyId}/${BuildConfig.DB_REF_WORK}/${employeeId}/${date.year}/${date.monthOfYear}/${date.dayOfMonth}"
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