package ke.co.shambapay.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import ke.co.shambapay.R
import ke.co.shambapay.utils.DateTimeExtensions.dateFormat
import ke.co.shambapay.utils.DateTimeExtensions.dateFormatDayFullMonthYear
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.Years
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

fun DateTime.toFullMonthYearString(): String {
    return this.toString(dateFormatDayFullMonthYear)
}

fun DateTime.toMonthYearString(): String {
    return this.toString(dateFormat)
}

fun DateTime.toMonthYearDuration(context: Context, dateTime: DateTime): String {
    val years = Years.yearsBetween(dateTime.withTime(0, 0, 0, 0), this.withTime(0, 0, 0, 0)).years
    val months = Months.monthsBetween(dateTime.withTime(0, 0, 0, 0), this.withTime(0, 0, 0, 0)).months % 12

    return (
           (if (years > 0) String.format(context.resources.getQuantityString(R.plurals.plural_year, years), years) else "") +
           (if (years > 0 && months > 0) " " else "") +
           if (months > 0) String.format(context.resources.getQuantityString(R.plurals.plural_month, months), months) else ""
           )
}

fun Activity.showDatePicker (
    onResult: (date: DateTime) -> Unit = {}
){
    val c = Calendar.getInstance()
    val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
        onResult(DateTime.now().withDate(year, monthOfYear+1, dayOfMonth))
    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    dpd.show()
}

//M -> 9
//MM -> 09
//MMM -> Sep
//MMMM -> September

object DateTimeExtensions {
    private const val FULL_MONTH_YEAR = "MMMM yyyy"
    private const val DATE_FORMAT = "d MMMM yyyy"

    val dateFormatDayFullMonthYear: DateTimeFormatter = DateTimeFormat.forPattern(FULL_MONTH_YEAR)
    val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT)
}
