package james.learning.whatsappclone.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Chat(private var chatId: String = "", var chatMessage: String = "", var time: Timestamp = Timestamp.now(), var senderId: String = "", var contactId: String = ""): Parcelable{
    constructor(parcel: Parcel) : this (
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(chatMessage)
        parcel.writeParcelable(time, flags)
        parcel.writeString(senderId)
        parcel.writeString(contactId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (chatId != other.chatId) return false
        if (chatMessage != other.chatMessage) return false
        if (time != other.time) return false
        if (senderId != other.senderId) return false
        if (contactId != other.contactId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + chatMessage.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + contactId.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }

}