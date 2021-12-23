package james.learning.whatsappclone.fragment

import android.content.Intent
import android.database.Cursor
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
import android.widget.Toast
import james.learning.whatsappclone.data.Constants.selectedImageVideo
import java.lang.Exception


class StatusFragment : Fragment() {
    lateinit var view: FragmentStatusBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = FragmentStatusBinding.inflate(layoutInflater)
        val thisActivity = activity as MainActivity
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
                            val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
                            val cursor: Cursor? = thisActivity.contentResolver.query(
                                data,
                                filePathColumn, null, null, null
                            )
                            if (cursor != null) {
                                cursor.moveToFirst()
                                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                                val filePath: String = cursor.getString(columnIndex)
                                cursor.close()
                                try {
                                    val mp: MediaPlayer = MediaPlayer.create(thisActivity, Uri.parse(filePath))
                                    val duration = mp.duration
                                    mp.release()
                                    if (duration / 1000 > 30) {
                                        Toast.makeText(thisActivity, "Please select only videos that are 30 seconds long", Toast.LENGTH_SHORT).show()
                                    } else {
                                        selectedItems.add(ImageVideo(data, video = true))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            selectedItems.add(ImageVideo(data, video = false))
                        }
                    }
                    selectedImageVideo = selectedItems
                    start
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