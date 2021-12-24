package james.learning.whatsappclone.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import james.learning.whatsappclone.data.Constants.CONVERSATIONS
import james.learning.whatsappclone.data.Constants.USERS
import james.learning.whatsappclone.data.Chat
import james.learning.whatsappclone.data.UserProfile
import james.learning.whatsappclone.databinding.PleaseWaitDialogBinding
import java.lang.ref.WeakReference
import java.util.*

open class BaseActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialog: Dialog
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    val userProfile = UserProfile()
    private lateinit var pleaseWaitView : PleaseWaitDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        onlineStatus(true)
    }

    fun getUniqueId() = UUID.randomUUID().toString()

    fun getConversationContacts() : ArrayList<String>{
        val friends = ArrayList<String>()
        conversationCollection(getCurrentUserId())
            .get().addOnCompleteListener {
            for (document in it.result) {
                friends.add(document.toObject(Chat::class.java).contactId)
            }
        }
        return friends
    }

    fun withEmailAndPassword(activity: Activity, email: String, password: String) {
        showProgressDialog()
        when (activity) {
            is SignUpActivity -> {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val imageUri = activity.getImage()
                        val currentUser = getCurrentUser()
                        if (currentUser != null) {
                            userProfile.userEmail = currentUser.email!!
                            if (imageUri != null) {
                                ProfileImageUploadTask(activity,firebaseStorage,getCurrentUserId(),imageUri).execute()
                            } else {
                                updateDB(getCurrentUserId(),activity)
                            }
                        }
                    }
                }
            }
            is LoginActivity -> {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    closeDialog()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    showSnackBar(it.message!!,activity.view.root)
                }
            }
        }
    }

    private fun showSnackBar(text: String, view: View) {
        val snackbar = Snackbar.make(view,text,Snackbar.LENGTH_SHORT)
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(Color.WHITE)
        snackbar.setBackgroundTint(Color.RED)
        snackbar.show()
    }

    fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.setCanceledOnTouchOutside(false)
        pleaseWaitView = PleaseWaitDialogBinding.inflate(layoutInflater)
        dialog.setContentView(pleaseWaitView.root)
        dialog.show()
    }

    fun closeDialog() {
        dialog.dismiss()
    }

    fun onlineStatus(status: Boolean) {
        val currentUser = getCurrentUserId()
        if (currentUser != "") {
            val map = HashMap<String, Boolean>()
            map["online"] = status
            FirebaseFirestore.getInstance().collection(USERS).document(currentUser)
                .set(map, SetOptions.merge())
        }
    }

    fun updateDB(userId: String, activity: SignUpActivity) {
        firestore.collection(USERS).document(userId).set(
            userProfile,
            SetOptions.merge()
        ).addOnCompleteListener {
            closeDialog()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            showSnackBar(it.message!!, activity.view.root)
        }
    }

    fun userExists(): Boolean = firebaseAuth.currentUser != null

    fun getCurrentUser() = firebaseAuth.currentUser

    fun getCurrentUserId(): String {
        val currentUserDetails = getCurrentUser()
        if (currentUserDetails != null)
            return currentUserDetails.uid
        return ""
    }

    fun conversationCollection(currentUserId: String) = firestore.collection(USERS)
        .document(currentUserId)
        .collection(CONVERSATIONS)

    fun chatDocument(first: String, second: String) = conversationCollection(first).document(second)

    class ProfileImageUploadTask(activity: SignUpActivity, private val firebaseStorage: FirebaseStorage, private val userId: String, private val imageUri: Uri): AsyncTask<Int, Int, String>() {
        private val baseActivity = WeakReference(activity)
        private var uploadResult: UploadTask? = null

        override fun doInBackground(vararg p0: Int?): String {
            uploadResult = firebaseStorage.reference.child("$userId.png").putFile(imageUri)
            return ""
        }

        override fun onPostExecute(result: String?) {
            val activity = baseActivity.get()!!
            var downloadResultUrl: String
            uploadResult?.addOnSuccessListener { task ->
                task.storage.downloadUrl.addOnSuccessListener { downloadResult ->
                    downloadResultUrl = downloadResult.toString()
                    activity.userProfile.imageUrl = downloadResultUrl
                    activity.updateDB(userId, activity)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onlineStatus(true)
    }
}