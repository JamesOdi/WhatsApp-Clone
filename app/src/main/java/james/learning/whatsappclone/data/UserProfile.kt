package james.learning.whatsappclone.data

import android.os.Parcel
import android.os.Parcelable

data class UserProfile(var imageUrl: String? = null, var userEmail: String= "", var online: Boolean = true): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(userEmail)
        parcel.writeByte(if (online) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserProfile

        if (imageUrl != other.imageUrl) return false
        if (userEmail != other.userEmail) return false
        if (online != other.online) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageUrl?.hashCode() ?: 0
        result = 31 * result + userEmail.hashCode()
        result = 31 * result + online.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<UserProfile> {
        override fun createFromParcel(parcel: Parcel): UserProfile {
            return UserProfile(parcel)
        }

        override fun newArray(size: Int): Array<UserProfile?> {
            return arrayOfNulls(size)
        }
    }
}
