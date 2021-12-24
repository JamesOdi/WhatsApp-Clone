package james.learning.whatsappclone.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import james.learning.whatsappclone.activity.ImageVideoStatusActivity
import james.learning.whatsappclone.data.Constants.COMMENT
import james.learning.whatsappclone.data.Constants.IS_VIDEO
import james.learning.whatsappclone.data.Constants.MP4
import james.learning.whatsappclone.data.Constants.PNG
import james.learning.whatsappclone.data.Constants.STATUS
import james.learning.whatsappclone.data.Constants.UPLOADS
import james.learning.whatsappclone.data.Constants.UPLOAD_URI
import james.learning.whatsappclone.data.ImageVideo
import james.learning.whatsappclone.databinding.StatusImageVideoBinding
import java.util.*

class ImageVideoViewPagerAdapter(val activity: ImageVideoStatusActivity, val context: Context, private val list: ArrayList<ImageVideo>): PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = list.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val status = StatusImageVideoBinding.inflate(LayoutInflater.from(context))
        val itemUri = list[position]
        val comment = itemUri.comment
        status.comment.setText(comment)
        if (itemUri.isVideo) {
            status.videoViewContent.visibility = VISIBLE
            status.imageViewContent.visibility = GONE
            status.videoViewContent.setVideoURI(itemUri.uri)
            status.videoViewContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus){
                    status.videoViewContent.start()
                } else {
                    status.videoViewContent.pause()
                }
            }
        } else {
            status.videoViewContent.visibility = GONE
            status.imageViewContent.visibility = VISIBLE
            Glide.with(context)
                .load(itemUri.uri)
                .into(status.imageViewContent)
        }
        status.comment.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(characters: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (characters != null && characters.isNotEmpty()) {
                    list[position].comment = characters.toString()
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        val storage = FirebaseStorage.getInstance()
        val userId = activity.getCurrentUserId()

        status.sendBtn.setOnClickListener {
            activity.showProgressDialog()
            val location = FirebaseFirestore.getInstance().collection(STATUS).document(userId).collection(UPLOADS)
            for (item in list) {
                val type = if (item.isVideo)
                    MP4
                else
                    PNG
                storage.reference.child("${UUID.randomUUID()}.$type").putFile(item.uri!!).addOnSuccessListener { uploadResultTask ->
                    uploadResultTask.storage.downloadUrl.addOnSuccessListener { downloadResult ->
                        val downloadResultUrl: String = downloadResult.toString()
                        val map = HashMap<String, Any>()
                        map[UPLOAD_URI] = downloadResultUrl
                        map[COMMENT] = item.comment
                        map[IS_VIDEO] = item.isVideo
                        location.document(UUID.randomUUID().toString()).set(map, SetOptions.merge())
                    }
                }
            }
            activity.closeDialog()
            activity.upload()
        }
        container.addView(status.root)
        return status.root
    }

}

