package ke.co.shambapay.data.model

import android.os.Parcel
import android.os.Parcelable

data class UserEntity (
    var id: String = "",
    val companyId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: Long = 0,
    val areaCode: Int = 0,
    val userType: UserType,
    val fcmToken: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readInt(),
        UserType.valueOf(parcel.readString().toString()),
        parcel.readString().toString()
    )

    constructor(): this(
        id = "",
        companyId = "",
        firstName = "",
        lastName = "",
        email = "",
        phone = 0,
        areaCode = 0,
        userType = UserType.ADMIN,
        fcmToken = ""
    )

    fun getFullName(): String = "$firstName $lastName"
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(companyId)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeLong(phone)
        parcel.writeInt(areaCode)
        parcel.writeString(userType.name)
        parcel.writeString(fcmToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserEntity> {
        override fun createFromParcel(parcel: Parcel): UserEntity {
            return UserEntity(parcel)
        }

        override fun newArray(size: Int): Array<UserEntity?> {
            return arrayOfNulls(size)
        }
    }
}
