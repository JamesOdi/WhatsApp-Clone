package james.learning.whatsappclone.activity

import android.os.Bundle
import james.learning.whatsappclone.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    lateinit var view: ActivityLoginBinding
    var email = ""
    var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.logInBtn.setOnClickListener {
            if (verifiedInputs()) {
                withEmailAndPassword(this,email, password)
            }
        }
    }

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