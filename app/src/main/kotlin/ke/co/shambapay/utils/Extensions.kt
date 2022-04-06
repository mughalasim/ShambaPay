package ke.co.shambapay.utils

import android.telephony.PhoneNumberUtils
import android.util.Patterns
import java.util.regex.Pattern

fun String.isInValidPhone(): Boolean {
    val pattern: Pattern = Pattern.compile("^254(7(?:[12][0-9]|0[0-8]|(9[0-2]))[0-9]{6})\$")
    return (!PhoneNumberUtils.isGlobalPhoneNumber(this) || !pattern.matcher(this).matches())
}

fun String.isInvalidEmail(): Boolean {
    return !Patterns.EMAIL_ADDRESS.matcher(this).matches()
}