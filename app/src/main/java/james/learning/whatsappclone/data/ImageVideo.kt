package james.learning.whatsappclone.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ImageVideo(var uri: Uri? = null, var isVideo: Boolean = false, var comment: String = ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeByte(if (isVideo) 1 else 0)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageVideo

        if (uri != other.uri) return false
        if (isVideo != other.isVideo) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri?.hashCode() ?: 0
        result = 31 * result + isVideo.hashCode()
        result = 31 * result + (comment.hashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<ImageVideo> {
        override fun createFromParcel(parcel: Parcel): ImageVideo {
            return ImageVideo(parcel)
        }

        override fun newArray(size: Int): Array<ImageVideo?> {
            return arrayOfNulls(size)
        }
    }
}

