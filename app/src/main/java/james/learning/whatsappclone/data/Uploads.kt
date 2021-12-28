package james.learning.whatsappclone.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Uploads(var uploadUrl: String= "", var uploadTime: Timestamp = Timestamp.now(), var views: List<String>? =null, var isVideo: Boolean = false, var comment: String= ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.createStringArrayList(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uploadUrl)
        parcel.writeParcelable(uploadTime, flags)
        parcel.writeStringList(views)
        parcel.writeByte(if (isVideo) 1 else 0)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Uploads

        if (uploadUrl != other.uploadUrl) return false
        if (uploadTime != other.uploadTime) return false
        if (views != other.views) return false
        if (isVideo != other.isVideo) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uploadUrl.hashCode()
        result = 31 * result + uploadTime.hashCode()
        result = 31 * result + (views?.hashCode() ?: 0)
        result = 31 * result + isVideo.hashCode()
        result = 31 * result + comment.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<Uploads> {
        override fun createFromParcel(parcel: Parcel): Uploads {
            return Uploads(parcel)
        }

        override fun newArray(size: Int): Array<Uploads?> {
            return arrayOfNulls(size)
        }
    }


}