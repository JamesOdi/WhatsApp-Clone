package james.learning.whatsappclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import james.learning.whatsappclone.data.UserProfile
import james.learning.whatsappclone.databinding.ContactsBinding

class ContactsAdapter(val context: Context, options: FirestoreRecyclerOptions<UserProfile>):FirestoreRecyclerAdapter<UserProfile, ContactsAdapter.VH>(options)  {
    var onClickListener: OnClickListener? = null

    class VH(val view: ContactsBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): VH {
        return VH(ContactsBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int, profile: UserProfile) {
        if (profile.imageUrl != "" && profile.imageUrl != null) {
            Glide.with(context)
                .load(profile.imageUrl)
                .circleCrop()
                .into(holder.view.userProfileImage)
        }
        holder.view.userEmail.text = profile.userEmail
        holder.view.root.setOnClickListener {
            onClickListener?.onClick(this.snapshots.getSnapshot(position).id)
        }
    }

    fun setOnClick(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(clickedUserId: String)
    }
}