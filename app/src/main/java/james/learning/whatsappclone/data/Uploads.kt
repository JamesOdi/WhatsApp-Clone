package james.learning.whatsappclone.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Uploads(var uploadUri: String= "", var uploadTime: Timestamp = Timestamp.now(), var views: List<String>? =null, var type: String = "", var comment: String= ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.createStringArrayList(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uploadUri)
        parcel.writeParcelable(uploadTime, flags)
        parcel.writeStringList(views)
        parcel.writeString(type)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Uploads

        if (uploadUri != other.uploadUri) return false
        if (uploadTime != other.uploadTime) return false
        if (views != other.views) return false
        if (type != other.type) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uploadUri.hashCode()
        result = 31 * result + uploadTime.hashCode()
        result = 31 * result + (views?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
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