package james.learning.whatsappclone.fragment

import android.content.Intent
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import james.learning.whatsappclone.activity.MainActivity
import james.learning.whatsappclone.activity.TextStatusActivity
import james.learning.whatsappclone.data.ImageVideo
import james.learning.whatsappclone.databinding.FragmentStatusBinding
import java.util.ArrayList
import android.media.MediaPlayer
import android.net.Uri

import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import james.learning.whatsappclone.activity.ImageVideoStatusActivity
import james.learning.whatsappclone.data.Constants.selectedImageVideoUri
import java.lang.Exception


class StatusFragment : Fragment() {
    lateinit var view: FragmentStatusBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = FragmentStatusBinding.inflate(layoutInflater)
        val thisActivity = activity as MainActivity
        selectedImageVideoUri = null

        view.addTextUpload.setOnClickListener {
            startActivity(Intent(thisActivity, TextStatusActivity::class.java))
        }

        val image_video_result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            val intentData = result.data
            if (intentData != null) {
                val clipData = intentData.clipData
                if (clipData != null) {
                    val selectedItems: ArrayList<ImageVideo> = ArrayList()
                    for (position in 0 until clipData.itemCount){
                        val data = clipData.getItemAt(position).uri
                        if (data.toString().contains("video")){

                            val mmdr = MediaMetadataRetriever()
                            mmdr.setDataSource(thisActivity, data)
                            val time = mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val timeInMillis = time?.toLong()!!
                            if (timeInMillis / 1000 > 30) {
                                Toast.makeText(thisActivity, "Please select only videos that are 30 seconds long", Toast.LENGTH_SHORT).show()
                            } else {
                                selectedItems.add(ImageVideo(data, isVideo = true))
                            }
                            mmdr.release()
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
            image_video_result.launch(intent)
        }

        return view.root
    }
}