package james.learning.whatsappclone.fragment

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import james.learning.whatsappclone.activity.MainActivity
import james.learning.whatsappclone.activity.TextStatusActivity
import james.learning.whatsappclone.data.ImageVideo
import james.learning.whatsappclone.databinding.FragmentStatusBinding

import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import james.learning.whatsappclone.activity.ImageVideoStatusActivity
import james.learning.whatsappclone.data.Constants.STATUS
import james.learning.whatsappclone.data.Constants.UPLOADS
import james.learning.whatsappclone.data.Constants.UPLOAD_TIME
import james.learning.whatsappclone.data.Constants.selectedImageVideoUri
import james.learning.whatsappclone.data.Uploads
import james.learning.whatsappclone.databinding.StatusBottomSheetBinding
import java.text.SimpleDateFormat
import java.util.*

class StatusFragment : Fragment() {
    lateinit var view: FragmentStatusBinding
    lateinit var thisActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = FragmentStatusBinding.inflate(layoutInflater)
        thisActivity = activity as MainActivity
        selectedImageVideoUri = null

        view.addTextUpload.setOnClickListener {
            startActivity(Intent(thisActivity, TextStatusActivity::class.java))
        }

        val userId = thisActivity.getCurrentUserId()
        val location = FirebaseFirestore.getInstance().collection(STATUS)
        val currentUserUploads = location.getCurrentUserStatusItems(userId)

        val bottomSheet = BottomSheetDialog(thisActivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bottomSheet.behavior.peekHeight = thisActivity.display?.height!!
        }

        view.userStatus.setOnClickListener {
            val statusView = StatusBottomSheetBinding.inflate(layoutInflater)
            bottomSheet.setContentView(statusView.root)
            statusView.backBtn.setOnClickListener {
                bottomSheet.dismiss()
            }

            bottomSheet.show()
        }

        val imageVideoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            val intentData = result.data
            if (intentData != null) {
                val clipData = intentData.clipData
                if (clipData != null) {
                    val selectedItems: ArrayList<ImageVideo> = ArrayList()
                    for (position in 0 until clipData.itemCount){
                        val data = clipData.getItemAt(position).uri
                        if (data.toString().contains("video")){
                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(thisActivity, data)
                            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val timeInMillis = time?.toLong()!!
                            if (timeInMillis / 1000 > 30) {
                                Toast.makeText(thisActivity, "Please select only videos that are 30 seconds long", Toast.LENGTH_SHORT).show()
                            } else {
                                selectedItems.add(ImageVideo(data, isVideo = true))
                            }
                            retriever.release()
                        } else {
                            selectedItems.add(ImageVideo(data, isVideo = false))
                        }
                    }
                    selectedImageVideoUri = selectedItems
                    startActivity(Intent(thisActivity, ImageVideoStatusActivity::class.java))
                }
            }
        }
        view.addImageVideoUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/* video/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            imageVideoResult.launch(intent)
        }
        return view.root
    }

    private fun CollectionReference.getCurrentUserStatusItems(userId: String): ArrayList<Uploads> {
        val uploads: ArrayList<Uploads> = ArrayList()
        val location = this.document(userId).collection(UPLOADS).orderBy(UPLOAD_TIME, Query.Direction.ASCENDING)
        location.addSnapshotListener { value, error ->
            if (value != null) {
                for (upload in value.documents) {
                    uploads.add(upload.toObject(Uploads::class.java)!!)
                }
                if (uploads.isNotEmpty()) {
                    val uploadItem = uploads[uploads.size - 1]
                    val date = uploadItem.uploadTime.toDate()
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val dateResult = sdf.format(date)
                    view.uploadedTime.text = dateResult

                    val uploadUri = Uri.parse(uploadItem.uploadUrl)
                    if (uploadItem.isVideo) {
                        val mmr = MediaMetadataRetriever()
                        mmr.setDataSource(thisActivity, uploadUri)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val image = mmr.getImageAtIndex(0)
                            Glide.with(view.root)
                                .load(image)
                                .circleCrop()
                                .into(view.statusUpload)
                        }
                    } else {
                        Glide.with(view.root)
                            .load(uploadUri)
                            .circleCrop()
                            .into(view.statusUpload)
                    }

                }
            }
        }
        return uploads
    }

    private fun CollectionReference.getAllUserStatus(userId: String) {

    }

}