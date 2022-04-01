package ke.co.shambapay.ui

import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import java.util.*

class UiGlobalState {

    var user: UserEntity? = null
    var settings: SettingsEntity? = null

    fun clear(){
        user = null
        settings = null
    }

    fun getDropDownOptions(): List<String> {
        val response: MutableList<String> = mutableListOf()

        return if (settings == null || settings?.rates == null){
            response.add("No rates have been set, please contact your administrator")
            response
        } else {
            response.add("Select a rate")
            for (item in settings!!.rates){
                response.add("${item.value.jobType.name.lowercase().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }} in ${item.value.measurement} at a rate of ${item.value.rate}")
            }
            response
        }

    }

    fun getRateIdForIndex(index: Int): String {
        return if (settings == null || settings?.rates == null){
            ""
        } else {
            settings!!.rates.keys.elementAt(index)
        }
    }

    fun getTotalForRateIdAndUnit(rateId: String, unit:Double?): Double{
        return if (settings == null || settings?.rates == null){
            0.0
        } else {
            (settings!!.rates[rateId]?.rate ?: 0.0) * (unit ?: 0.0)
        }
    }

    fun isAdmin(): Boolean{
        return user!!.userType == UserType.ADMIN
    }

}