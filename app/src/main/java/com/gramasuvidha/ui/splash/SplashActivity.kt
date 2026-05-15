package com.gramasuvidha.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.gramasuvidha.R
import com.gramasuvidha.ui.admin.AdminMainActivity
import com.gramasuvidha.ui.auth.LoginActivity
import com.gramasuvidha.ui.citizen.CitizenMainActivity
import com.gramasuvidha.utils.AppLogger
import com.gramasuvidha.utils.LocaleHelper
import com.gramasuvidha.utils.PrefsManager

/**
 * Splash screen that displays the app logo and routes to the appropriate screen
 * based on saved login state.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLogger.d(TAG, "Splash screen started")
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun navigateToNextScreen() {
        val isLoggedIn = PrefsManager.isLoggedIn(this)
        val role = PrefsManager.getRole(this)
        AppLogger.d(TAG, "isLoggedIn=$isLoggedIn  role=$role")

        val intent = if (isLoggedIn) {
            when (role) {
                PrefsManager.ROLE_ADMIN -> {
                    AppLogger.i(TAG, "Navigating to → AdminMainActivity")
                    Intent(this, AdminMainActivity::class.java)
                }
                PrefsManager.ROLE_CITIZEN, PrefsManager.ROLE_GUEST -> {
                    AppLogger.i(TAG, "Navigating to → CitizenMainActivity (role=$role)")
                    Intent(this, CitizenMainActivity::class.java)
                }
                else -> {
                    AppLogger.i(TAG, "Navigating to → CitizenMainActivity (fallback)")
                    Intent(this, CitizenMainActivity::class.java)
                }
            }
        } else {
            AppLogger.i(TAG, "Not logged in → LoginActivity")
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
