package james.learning.whatsappclone.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import james.learning.whatsappclone.activity.BaseActivity
import james.learning.whatsappclone.activity.ContactsActivity
import james.learning.whatsappclone.data.Constants.TIME
import james.learning.whatsappclone.adapter.ChatContactAdapter
import james.learning.whatsappclone.data.Chat
import james.learning.whatsappclone.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    lateinit var view: FragmentChatsBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: ChatContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val thisActivity = activity as BaseActivity
        val userId = thisActivity.getCurrentUserId()

        view = FragmentChatsBinding.inflate(layoutInflater)
        firestore = FirebaseFirestore.getInstance()

        val messagesRV = view.messagesRv
        messagesRV.layoutManager = LinearLayoutManager(thisActivity)

        view.messageFab.setOnClickListener {
            startActivity(Intent(activity, ContactsActivity::class.java))
        }

        val query = thisActivity.conversationCollection(userId)
        val options = FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(query.orderBy(TIME, Query.Direction.DESCENDING), Chat::class.java)
            .build()

        adapter = ChatContactAdapter(thisActivity, options)
        messagesRV.adapter = adapter
        return view.root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }
}