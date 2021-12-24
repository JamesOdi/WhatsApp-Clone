package james.learning.whatsappclone.data

import android.os.Parcel
import android.os.Parcelable

data class StatusOwner(var userId: String = "", var friends: List<String>? = null, var userEmail: String = "", var userProfilePhoto: String = ""): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArrayList(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeStringList(friends)
        parcel.writeString(userEmail)
        parcel.writeString(userProfilePhoto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StatusOwner> {
        override fun createFromParcel(parcel: Parcel): StatusOwner {
            return StatusOwner(parcel)
        }

        override fun newArray(size: Int): Array<StatusOwner?> {
            return arrayOfNulls(size)
        }
    }
}
