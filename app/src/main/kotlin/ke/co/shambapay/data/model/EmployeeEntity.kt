package ke.co.shambapay.data.model

import android.os.Parcel
import android.os.Parcelable

data class EmployeeEntity (
    val id: String = "",
    val nationalId: Long? = 0,
    val firstName: String = "",
    val lastName: String = "",
    val cash: Double = 0.0,
    val advance: Double = 0.0,
    val misc: Double = 0.0,
    val nhif: String = "",
    val nssf: String = "",
    val phone: Long = 0,
    val areaCode: Int = 0,
    var isCaptured: Boolean? = null
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()!!,
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readInt(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    constructor(): this(
        id = "",
        nationalId = 0,
        firstName = "",
        lastName = "",
        cash = 0.0,
        advance = 0.0,
        misc = 0.0,
        nhif = "",
        nssf = "",
        phone = 0,
        areaCode = 0
    )

    fun getFullName(): String = "$firstName $lastName"

    fun getFullNameUnderScore(): String = "${firstName.trim()}_${lastName.trim()}"

    fun getNID(): String = if (nationalId == 0L) "Not set" else nationalId.toString()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeValue(nationalId)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeDouble(cash)
        parcel.writeDouble(advance)
        parcel.writeDouble(misc)
        parcel.writeString(nhif)
        parcel.writeString(nssf)
        parcel.writeLong(phone)
        parcel.writeInt(areaCode)
        parcel.writeValue(isCaptured)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmployeeEntity> {
        override fun createFromParcel(parcel: Parcel): EmployeeEntity {
            return EmployeeEntity(parcel)
        }

        override fun newArray(size: Int): Array<EmployeeEntity?> {
            return arrayOfNulls(size)
        }
    }
}
