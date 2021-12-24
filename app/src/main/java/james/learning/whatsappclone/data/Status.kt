package james.learning.whatsappclone.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Status(var uploadsUri: List<Uploads>?=null, var uploaderId: String = "", var uploaderName: String=""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Uploads),
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(uploadsUri)
        parcel.writeString(uploaderId)
        parcel.writeString(uploaderName)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Status

        if (uploadsUri != other.uploadsUri) return false
        if (uploaderId != other.uploaderId) return false
        if (uploaderName != other.uploaderName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uploadsUri?.hashCode() ?: 0
        result = 31 * result + uploaderId.hashCode()
        result = 31 * result + uploaderName.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<Status> {
        override fun createFromParcel(parcel: Parcel): Status {
            return Status(parcel)
        }

        override fun newArray(size: Int): Array<Status?> {
            return arrayOfNulls(size)
        }
    }


}


