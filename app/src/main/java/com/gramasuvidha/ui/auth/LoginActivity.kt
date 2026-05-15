package com.gramasuvidha.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.gramasuvidha.R
import com.gramasuvidha.ui.admin.AdminMainActivity
import com.gramasuvidha.ui.citizen.CitizenMainActivity
import com.gramasuvidha.utils.Constants
import com.gramasuvidha.utils.LocaleHelper
import com.gramasuvidha.utils.PrefsManager
import java.util.concurrent.TimeUnit

/**
 * Login Activity with two access modes:
 * 1. Admin login with username/password
 * 2. Citizen login with phone number (OTP via Firebase) or guest browse
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Role selection views
    private lateinit var btnAdminRole: MaterialButton
    private lateinit var btnCitizenRole: MaterialButton

    // Admin login views
    private lateinit var cardAdminLogin: MaterialCardView
    private lateinit var tilAdminUsername: TextInputLayout
    private lateinit var etAdminUsername: TextInputEditText
    private lateinit var tilAdminPassword: TextInputLayout
    private lateinit var etAdminPassword: TextInputEditText
    private lateinit var btnAdminLogin: MaterialButton

    // Citizen login views
    private lateinit var cardCitizenLogin: MaterialCardView
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etPhone: TextInputEditText
    private lateinit var tilOtp: TextInputLayout
    private lateinit var etOtp: TextInputEditText
    private lateinit var btnSendOtp: MaterialButton
    private lateinit var btnVerifyOtp: MaterialButton
    private lateinit var btnBrowseAsGuest: MaterialButton

    // Language toggle
    private lateinit var btnLanguageToggle: MaterialButton

    // Progress
    private lateinit var progressBar: ProgressBar

    private var verificationId: String = ""
    private var currentRole = "citizen"

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        initViews()
        setupListeners()
        showCitizenLogin() // Default to citizen
    }

    private fun initViews() {
        btnAdminRole = findViewById(R.id.btn_admin_role)
        btnCitizenRole = findViewById(R.id.btn_citizen_role)

        cardAdminLogin = findViewById(R.id.card_admin_login)
        tilAdminUsername = findViewById(R.id.til_admin_username)
        etAdminUsername = findViewById(R.id.et_admin_username)
        tilAdminPassword = findViewById(R.id.til_admin_password)
        etAdminPassword = findViewById(R.id.et_admin_password)
        btnAdminLogin = findViewById(R.id.btn_admin_login)

        cardCitizenLogin = findViewById(R.id.card_citizen_login)
        tilPhone = findViewById(R.id.til_phone)
        etPhone = findViewById(R.id.et_phone)
        tilOtp = findViewById(R.id.til_otp)
        etOtp = findViewById(R.id.et_otp)
        btnSendOtp = findViewById(R.id.btn_send_otp)
        btnVerifyOtp = findViewById(R.id.btn_verify_otp)
        btnBrowseAsGuest = findViewById(R.id.btn_browse_guest)

        btnLanguageToggle = findViewById(R.id.btn_language_toggle)
        progressBar = findViewById(R.id.progress_bar)

        updateLanguageButton()
    }

    private fun setupListeners() {
        // Role toggle
        btnAdminRole.setOnClickListener {
            currentRole = "admin"
            showAdminLogin()
        }

        btnCitizenRole.setOnClickListener {
            currentRole = "citizen"
            showCitizenLogin()
        }

        // Admin login
        btnAdminLogin.setOnClickListener { handleAdminLogin() }

        // Citizen OTP flow
        btnSendOtp.setOnClickListener { handleSendOtp() }
        btnVerifyOtp.setOnClickListener { handleVerifyOtp() }

        // Guest browse
        btnBrowseAsGuest.setOnClickListener {
            PrefsManager.saveLogin(this, PrefsManager.ROLE_GUEST, "Guest")
            navigateToCitizen()
        }

        // Language toggle
        btnLanguageToggle.setOnClickListener {
            LocaleHelper.toggleLanguage(this)
            recreate()
        }
    }

    private fun showAdminLogin() {
        cardAdminLogin.visibility = View.VISIBLE
        cardCitizenLogin.visibility = View.GONE
        btnAdminRole.alpha = 1.0f
        btnCitizenRole.alpha = 0.5f
    }

    private fun showCitizenLogin() {
        cardAdminLogin.visibility = View.GONE
        cardCitizenLogin.visibility = View.VISIBLE
        btnAdminRole.alpha = 0.5f
        btnCitizenRole.alpha = 1.0f
    }

    private fun handleAdminLogin() {
        val username = etAdminUsername.text.toString().trim()
        val password = etAdminPassword.text.toString().trim()

        if (username.isEmpty()) {
            tilAdminUsername.error = getString(R.string.error_required)
            return
        }
        if (password.isEmpty()) {
            tilAdminPassword.error = getString(R.string.error_required)
            return
        }

        tilAdminUsername.error = null
        tilAdminPassword.error = null

        if (username == Constants.ADMIN_USERNAME && password == Constants.ADMIN_PASSWORD) {
            PrefsManager.saveLogin(this, PrefsManager.ROLE_ADMIN, "Admin")
            navigateToAdmin()
        } else {
            Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendOtp() {
        val phone = etPhone.text.toString().trim()
        if (phone.isEmpty() || phone.length < 10) {
            tilPhone.error = getString(R.string.error_invalid_phone)
            return
        }
        tilPhone.error = null

        progressBar.visibility = View.VISIBLE

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    progressBar.visibility = View.GONE
                    signInWithCredential(credential, phone)
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "OTP sending failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    progressBar.visibility = View.GONE
                    verificationId = verId
                    tilOtp.visibility = View.VISIBLE
                    etOtp.visibility = View.VISIBLE
                    btnVerifyOtp.visibility = View.VISIBLE
                    btnSendOtp.text = getString(R.string.resend_otp)
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.otp_sent),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun handleVerifyOtp() {
        val otp = etOtp.text.toString().trim()
        if (otp.isEmpty() || otp.length != 6) {
            tilOtp.error = getString(R.string.error_invalid_otp)
            return
        }
        tilOtp.error = null

        progressBar.visibility = View.VISIBLE
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential, etPhone.text.toString().trim())
    }

    private fun signInWithCredential(credential: PhoneAuthCredential, phone: String) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    PrefsManager.saveLogin(this, PrefsManager.ROLE_CITIZEN, "Citizen", phone)
                    navigateToCitizen()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.otp_verification_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToAdmin() {
        startActivity(Intent(this, AdminMainActivity::class.java))
        finish()
    }

    private fun navigateToCitizen() {
        startActivity(Intent(this, CitizenMainActivity::class.java))
        finish()
    }

    private fun updateLanguageButton() {
        val currentLang = LocaleHelper.getLanguage(this)
        btnLanguageToggle.text = if (currentLang == "en") "ಕನ್ನಡ" else "English"
    }
}
