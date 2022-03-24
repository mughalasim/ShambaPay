package ke.co.shambapay.ui

import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity
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

        if (settings == null || settings?.rates == null){
            response.add("No rates have been set, please contact your administrator")
            return response
        } else {
            response.add("Select a rate")
            for (item in settings!!.rates){
                response.add("${item.value.jobType.name.lowercase().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }} in ${item.value.measurement} at a rate of ${item.value.rate}")
            }
            return response
        }

    }

    fun getRateIdForIndex(index: Int): String {
        if (settings == null || settings?.rates == null){
            return ""
        } else {
           return settings!!.rates.keys.elementAt(index)
        }
    }

    fun getTotalForRateIdAndUnit(rateId: String, unit:Double): Double{
        if (settings == null || settings?.rates == null){
            return 0.0
        } else {
            return settings!!.rates[rateId]?.rate ?: 0.0 * unit
        }
    }

}