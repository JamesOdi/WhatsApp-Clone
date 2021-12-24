package james.learning.whatsappclone.activity

import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import james.learning.whatsappclone.adapter.ImageVideoViewPagerAdapter
import james.learning.whatsappclone.data.Constants
import james.learning.whatsappclone.data.Constants.IMAGE_URL
import james.learning.whatsappclone.data.Constants.USERS
import james.learning.whatsappclone.data.Constants.selectedImageVideoUri
import james.learning.whatsappclone.data.StatusOwner
import james.learning.whatsappclone.databinding.ActivityImageVideoStatusBinding
import java.util.*

class ImageVideoStatusActivity : BaseActivity() {
    private lateinit var view: ActivityImageVideoStatusBinding
    private var conversations: ArrayList<String>? = null
    private var userId: String? =null
    private var userEmail: String? = null
    private var profilePhoto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityImageVideoStatusBinding.inflate(layoutInflater)
        setContentView(view.root)
        userId = getCurrentUserId()
        userEmail = getCurrentUser()?.email
        conversations = getConversationContacts()
        view.viewPager.adapter = ImageVideoViewPagerAdapter(this,
            this, selectedImageVideoUri!!
        )
        FirebaseFirestore.getInstance().collection(USERS).document(userId!!).addSnapshotListener { value, error ->
            if (value != null) {
                profilePhoto = value.getString(IMAGE_URL).toString()
            }
        }
    }

    fun upload() {
        val location = FirebaseFirestore.getInstance().collection(Constants.STATUS).document(userId!!)
        location.set(StatusOwner(userId!!, conversations, userEmail!!, profilePhoto), SetOptions.merge())
        onBackPressed()
    }
}