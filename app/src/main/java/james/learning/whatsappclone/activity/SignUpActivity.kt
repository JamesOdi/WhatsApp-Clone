package james.learning.whatsappclone.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import james.learning.whatsappclone.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity() {
    lateinit var view: ActivitySignUpBinding
    private var email = ""
    private var password = ""
    private var imageData : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(view.root)

        val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data!!.data != null) {
                imageData = result.data!!.data!!
                Glide.with(this)
                    .load(imageData)
                    .circleCrop()
                    .into(view.personImage)
            }
        }

        view.personImage.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK)
            imageIntent.type = "image/*"
            imageResult.launch(imageIntent)
        }

        view.signInBtn.setOnClickListener {
            if (verifiedInputs()) {
                withEmailAndPassword(this,email, password)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (userExists()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun getImage() = imageData

    private fun verifiedInputs(): Boolean {
        val emailInput = view.emailInput.text.toString().trim()
        val passwordInput = view.passwordInput.text.toString().trim()

        if (emailInput.isEmpty()) {
            view.emailLayout.error = "Please Fill"
            return false
        } else {
            email = emailInput
        }

        if (passwordInput.isEmpty() or (passwordInput.length < 8)) {
            view.passwordLayout.error = "Password must be at least 8 characters"
            return false
        } else {
            password = passwordInput
        }
        return true
    }
}