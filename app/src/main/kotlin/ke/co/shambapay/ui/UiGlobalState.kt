package ke.co.shambapay.ui

import android.app.Activity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import ke.co.shambapay.data.intent.BulkSMSData
import ke.co.shambapay.data.model.SettingsEntity
import ke.co.shambapay.data.model.UserEntity
import ke.co.shambapay.data.model.UserType
import ke.co.shambapay.ui.activities.StartActivity
import java.util.*

class UiGlobalState {

    var user: UserEntity? = null
    var settings: SettingsEntity? = null
    var bulkSMS: List<BulkSMSData>? = null

    fun logout(activity: Activity) {
        clear()
        FirebaseAuth.getInstance().signOut()
        activity.startActivity(Intent(activity, StartActivity::class.java).apply {
            /*If you want to add any intent extras*/
        })
        activity.finish()
    }

    fun clear() {
        user = null
        settings = null
        bulkSMS = null
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