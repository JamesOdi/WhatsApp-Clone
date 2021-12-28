package james.learning.whatsappclone.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import james.learning.whatsappclone.data.Constants
import james.learning.whatsappclone.data.Constants.STATUS
import james.learning.whatsappclone.data.Status
import james.learning.whatsappclone.data.StatusOwner
import james.learning.whatsappclone.data.Uploads
import james.learning.whatsappclone.databinding.ActivityTextStatusBinding

class TextStatusActivity : BaseActivity() {
    lateinit var view: ActivityTextStatusBinding
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityTextStatusBinding.inflate(layoutInflater)
        setContentView(view.root)
        
        firestore = FirebaseFirestore.getInstance()
        val currentUserId = getCurrentUserId()
        val statusLocation = firestore.collection(STATUS).document(currentUserId)

        view.statusText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(characterSet: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (characterSet != null && characterSet.isNotEmpty()) {
                    view.addUpload.visibility = VISIBLE
                } else {
                    view.addUpload.visibility = GONE
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        view.addUpload.setOnClickListener {
            val text = view.statusText.text.toString().trim()
            statusLocation  .set(StatusOwner(getCurrentUserId(), getConversationContacts()), SetOptions.merge()).addOnCompleteListener {
                statusLocation.collection(Constants.UPLOADS).document().set(Status(listOf(Uploads(text, views = listOf(currentUserId), isVideo = false)),
                    uploaderId = currentUserId,
                    uploaderName = getCurrentUser()?.email!!),
                    SetOptions.merge()).addOnCompleteListener {
                    onBackPressed()
                }.addOnFailureListener {
                    Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}