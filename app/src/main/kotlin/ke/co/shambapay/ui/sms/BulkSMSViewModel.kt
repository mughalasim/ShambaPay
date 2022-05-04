package ke.co.shambapay.ui.sms

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import ke.co.shambapay.R
import ke.co.shambapay.data.intent.BulkSMSData
import ke.co.shambapay.ui.UiGlobalState
import ke.co.shambapay.ui.base.BaseState
import ke.co.shambapay.ui.base.BaseViewModel

class BulkSMSViewModel(
    private val globalState: UiGlobalState
) : BaseViewModel() {

    sealed class Response{
        data class Data(val list: List<BulkSMSData>, val message: String = ""): Response()
    }

    fun updateList(defaultMessage: String){
        if (globalState.bulkSMS.isNullOrEmpty()) {
            _state.postValue(BaseState.Success(Response.Data(list = emptyList(), message = defaultMessage)))
            return
        }
        _state.postValue(BaseState.Success(Response.Data(list = globalState.bulkSMS!!)))
    }

    fun sendBulkSMS(activity: Activity) {
        _state.postValue(BaseState.UpdateUI(true, "Sending out SMS's, Please wait..."))

        var isSuccessful = true

        globalState.bulkSMS?.forEach { smsData ->
            if (failedToSendSMS(activity, smsData.phone.toString(), "Hi ${smsData.fullName}, ${activity.getString(R.string.app_name)} owes you Ksh ${String.format("%,.2f", smsData.amount).trim()}")) {
                _state.postValue(BaseState.UpdateUI(false, "Something went wrong trying to send an sms out to ${smsData.fullName} on ${smsData.phone}"))
                isSuccessful = false
                return@forEach
            } else {
                Log.d(BulkSMSViewModel::javaClass.name, "Successfully sent sms to ${smsData.fullName} on ${smsData.phone}")
                _state.postValue(BaseState.UpdateUI(false, "Successfully sent sms to ${smsData.fullName} on ${smsData.phone}"))
                Thread.sleep(800)
            }
        }
        if (isSuccessful) {
            globalState.bulkSMS = null
            updateList("Successfully sent Bulk SMS")
        }

    }

    @Suppress("DEPRECATION")
    private fun failedToSendSMS(activity: Activity, phoneNumber: String, message: String): Boolean {
        return try {
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE)
            val manager = SmsManager.getDefault()
            manager.sendTextMessage(phoneNumber, phoneNumber, message, pendingIntent, pendingIntent)
            false
        } catch (e: Exception){
            Log.e(BulkSMSViewModel::javaClass.name, "Error sending SMS: " + e.localizedMessage)
            true
        }
    }
}