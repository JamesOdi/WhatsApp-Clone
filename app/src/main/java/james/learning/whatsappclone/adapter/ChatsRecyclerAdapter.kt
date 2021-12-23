package james.learning.whatsappclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import james.learning.whatsappclone.data.Chat
import james.learning.whatsappclone.databinding.ChatViewBinding

class ChatsRecyclerAdapter(val context: Context, private val currentUserId: String, options: FirestoreRecyclerOptions<Chat>): FirestoreRecyclerAdapter<Chat, ChatsRecyclerAdapter.VH>(options) {
    class VH(val view: ChatViewBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ChatViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int, model: Chat) {
        if (currentUserId == model.senderId) {
            holder.view.sent.visibility = VISIBLE
            holder.view.sent.text = model.chatMessage
        } else {
            holder.view.receive.visibility = VISIBLE
            holder.view.receive.text = model.chatMessage
        }
    }
}