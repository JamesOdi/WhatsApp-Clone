package james.learning.whatsappclone.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import james.learning.whatsappclone.data.Constants.CHATS
import james.learning.whatsappclone.data.Constants.IMAGE_URL
import james.learning.whatsappclone.data.Constants.ONLINE_STATUS
import james.learning.whatsappclone.data.Constants.TIME
import james.learning.whatsappclone.data.Constants.USERS
import james.learning.whatsappclone.data.Constants.USER_EMAIL
import james.learning.whatsappclone.data.Constants.USER_ID
import james.learning.whatsappclone.adapter.ChatsRecyclerAdapter
import james.learning.whatsappclone.data.Chat
import james.learning.whatsappclone.databinding.ActivityChatPageBinding

class ChatPageActivity : BaseActivity() {
    private lateinit var view: ActivityChatPageBinding
    private lateinit var currentUserChats: CollectionReference
    private lateinit var otherUserChats: CollectionReference
    private lateinit var clickedUserId : String
    private lateinit var adapter: ChatsRecyclerAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityChatPageBinding.inflate(layoutInflater)
        setContentView(view.root)

        userId = getCurrentUserId()
        clickedUserId = intent.getStringExtra(USER_ID).toString()
        currentUserChats = chatDocument(userId, clickedUserId).collection(CHATS)
        otherUserChats = chatDocument(clickedUserId, userId).collection(CHATS)

        FirebaseFirestore.getInstance().collection(USERS).document(clickedUserId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val userImage = value.getString(IMAGE_URL)
                    if (userImage != null && userImage != "") {
                        Glide.with(view.root)
                            .load(userImage)
                            .circleCrop()
                            .into(view.userProfileImage)
                    }
                    val online = value.getBoolean(ONLINE_STATUS)
                    var status = "offline"
                    if (online != null && online) {
                        status = ONLINE_STATUS
                    }
                    view.onlineStatus.text = status
                    view.username.text = value.getString(USER_EMAIL)
                }
            }

        view.chatsRv.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(currentUserChats.orderBy(TIME, Query.Direction.ASCENDING), Chat::class.java)
            .build()
        adapter = ChatsRecyclerAdapter(this, userId, options)
        view.chatsRv.adapter = adapter

        view.sendBtn.setOnClickListener {
            sendMessage()
        }
        view.backBtn.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun sendMessage() {
        val message = view.inputText.text.toString().trim()
        val chatId = getUniqueId()
        if (!TextUtils.isEmpty(message)) {
            val chat = Chat(
                chatId = chatId,
                chatMessage = message,
                senderId = userId,)

            chat.contactId = clickedUserId
            setValuesToCurrentUser(chat)

            chat.contactId = userId
            setValueToOtherUser(chat)

            chat.contactId = clickedUserId
            currentUserChats.document(chatId).set(chat, SetOptions.merge()).addOnCompleteListener {
                adapter.notifyItemInserted(adapter.itemCount)
                view.inputText.text.clear()

                chat.contactId = userId
                otherUserChats.document(chatId).set(chat, SetOptions.merge())
            }
        }
    }

    private fun setValuesToCurrentUser(chat: Chat): Task<Void> {
        return chatDocument(userId, clickedUserId).set(chat, SetOptions.merge())
    }

    private fun setValueToOtherUser(chat: Chat): Task<Void> {
        return chatDocument(clickedUserId, userId).set(chat, SetOptions.merge())
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}