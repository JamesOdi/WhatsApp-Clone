package james.learning.whatsappclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import james.learning.whatsappclone.data.Chat
import james.learning.whatsappclone.activity.ChatPageActivity
import james.learning.whatsappclone.data.Constants
import james.learning.whatsappclone.data.Constants.IMAGE_URL
import james.learning.whatsappclone.data.Constants.USERS
import james.learning.whatsappclone.data.Constants.USER_EMAIL
import james.learning.whatsappclone.databinding.ChatContactViewBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatContactAdapter(val context: Context, options: FirestoreRecyclerOptions<Chat>): FirestoreRecyclerAdapter<Chat, ChatContactAdapter.VH>(options) {

    class VH(val view: ChatContactViewBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ChatContactViewBinding
            .inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int, model: Chat) {
        val currentView = holder.view
        currentView.message.text = model.chatMessage

        FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(model.contactId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    if (value.exists()) {
                        currentView.userEmail.text = value.getString(USER_EMAIL)
                        Glide.with(context)
                            .load(value.getString(IMAGE_URL))
                            .circleCrop()
                            .into(currentView.userProfileImage)
                    }
                }
        }

        val date = model.time.toDate()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateResult = sdf.format(date)
        currentView.time.text = dateResult

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChatPageActivity::class.java).apply {
                putExtra(Constants.USER_ID, model.contactId)
            })
        }
    }
}