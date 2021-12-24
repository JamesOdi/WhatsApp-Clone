package james.learning.whatsappclone.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bumptech.glide.Glide
import james.learning.whatsappclone.data.Constants
import james.learning.whatsappclone.databinding.FragmentStatusItemsBinding

class StatusItemsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val items = Constants.selectedImageVideoUri
        val view = FragmentStatusItemsBinding.inflate(layoutInflater)
        if (items != null) {
            for (item in items) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (item.isVideo) {
                        view.videoViewContent.visibility = VISIBLE
                        view.imageViewContent.visibility = GONE
                        view.videoViewContent.setVideoURI(item.uri)
                    } else {
                        view.videoViewContent.visibility = VISIBLE
                        view.imageViewContent.visibility = GONE
                        Glide.with(view.root)
                            .load(item.uri)
                            .into(view.imageViewContent)
                        view.videoViewContent.setVideoURI(null)
                    }
                }, 30000)
            }
        }
        return view.root
    }
}