package com.example.jeeek.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.jeeek.R
import com.example.jeeek.databinding.ActivityAuthBinding
import com.example.jeeek.domain.EmailPasswordPayload
import com.example.jeeek.repository.AuthRepository
import com.example.jeeek.utils.validateForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class AuthActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding
    private lateinit var repository: AuthRepository

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_auth
        )
        // Buttons
        binding.emailSignInButton.setOnClickListener(this)
        binding.emailCreateAccountButton.setOnClickListener(this)
        binding.signOutButton.setOnClickListener(this)
        binding.verifyEmailButton.setOnClickListener(this)
        binding.reloadButton.setOnClickListener(this)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        repository = AuthRepository(auth)
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String) {
        Timber.d("createAccount:$email")
        if (!validateForm(email, password)) {
            Timber.d("validate failed.")
            return
        }
        Timber.d("validate ok.")

        coroutineScope.launch {
            val p = EmailPasswordPayload(
                email = email,
                password = password
            )
            repository.signup(p)

            val user = auth.currentUser
            updateUI(user)
        }
    }

    private fun signIn(email: String, password: String) {
        Timber.d("signIn:$email")
        if (!validateForm(email, password)) {
            return
        }

        coroutineScope.launch {
            val p = EmailPasswordPayload(
                email = email,
                password = password
            )
            repository.signin(p)

            val user = auth.currentUser
            updateUI(user)
        }
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        // Disable button
        binding.verifyEmailButton.isEnabled = false

        // Send verification email
        // [START send_email_verification]
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                // [START_EXCLUDE]
                // Re-enable button
                binding.verifyEmailButton.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(baseContext,
                        "Verification email sent to ${user.email} ",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Timber.e("sendEmailVerification")
                    Toast.makeText(baseContext,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
                // [END_EXCLUDE]
            }
        // [END send_email_verification]
    }

    private fun reload() {
        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
                Toast.makeText(this@AuthActivity,
                    "Reload successful!",
                    Toast.LENGTH_SHORT).show()
            } else {
                Timber.e("reload")
                Toast.makeText(this@AuthActivity,
                    "Failed to reload user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            binding.status.text = getString(R.string.signin_status_fmt,
                user.email, user.isEmailVerified)
            binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)

            binding.emailPasswordButtons.visibility = View.GONE
            binding.emailPasswordFields.visibility = View.GONE
            binding.signedInButtons.visibility = View.VISIBLE

            if (user.isEmailVerified) {
                binding.verifyEmailButton.visibility = View.GONE
            } else {
                binding.verifyEmailButton.visibility = View.VISIBLE
            }

            user.getIdToken(true).addOnSuccessListener {
                val idToken = it.token
                Timber.d(idToken)

                val intent = Intent(application, MainActivity::class.java)
                intent.putExtra(ID_TOKEN, idToken)
                startActivity(intent)
            }

        } else {
            binding.status.setText(R.string.signed_out)
            binding.detail.text = null

            binding.emailPasswordButtons.visibility = View.VISIBLE
            binding.emailPasswordFields.visibility = View.VISIBLE
            binding.signedInButtons.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.emailCreateAccountButton -> createAccount(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
            R.id.emailSignInButton -> signIn(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
            R.id.signOutButton -> signOut()
            R.id.verifyEmailButton -> sendEmailVerification()
            R.id.reloadButton -> reload()
        }
    }

    companion object {
        private const val TAG = "Auth"
        const val ID_TOKEN = "com.example.jeeek.ui.TEXTDATA"
    }
}
