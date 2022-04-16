package ke.co.shambapay.ui.sms

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.telephony.SmsManager
import ke.co.shambapay.R
import ke.co.shambapay.data.intent.BulkSMSData
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel


class BulkSMSViewModel(
    private val globalState: UiGlobalState
) : BaseViewModel() {

    init {
        _state.postValue(BaseState.UpdateUI(false, ""))
    }

    fun updateList(){
        if (globalState.bulkSMS.isNullOrEmpty()) {
            _state.postValue(BaseState.UpdateUI(false, "No numbers to send out SMS's to"))
            _state.postValue(BaseState.Success(emptyList<BulkSMSData>()))
            return
        }
        _state.postValue(BaseState.Success(globalState.bulkSMS))
    }

    fun sendBulkSMS(activity: Activity) {
        _state.postValue(BaseState.UpdateUI(true, "Sending out SMS's, Please wait..."))

        globalState.bulkSMS?.forEach { smsData ->
            if (failedToSendSMS(activity, smsData.phone.toString(), "Hi ${smsData.fullName}, ${activity.getString(R.string.app_name)} owes you Ksh ${String.format("%,.2f", smsData.amount).trim()}")) {
                _state.postValue(BaseState.UpdateUI(false, "Something went wrong trying to send an sms out to ${smsData.fullName} on ${smsData.phone}"))
               return@forEach
            } else {
                println("Successfully sent sms to ${smsData.fullName} on ${smsData.phone}")
                Thread.sleep(500)
            }
        }
        globalState.bulkSMS = null
        updateList()
    }

    @Suppress("DEPRECATION")
    fun failedToSendSMS(activity: Activity, phoneNumber: String, message: String): Boolean {
        return try {
            val sentPI: PendingIntent = PendingIntent.getBroadcast(activity, 0, Intent("SMS_SENT"), 0)
            val manager = SmsManager.getDefault()
            manager.sendTextMessage(phoneNumber, phoneNumber, message, sentPI, sentPI)
            false
        } catch (e: Exception){
            println(e.localizedMessage)
            true
        }
    }
}