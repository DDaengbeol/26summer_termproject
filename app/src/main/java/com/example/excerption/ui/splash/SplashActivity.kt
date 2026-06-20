package com.example.excerption.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.SessionManager
import com.example.excerption.ui.auth.LoginActivity
import com.example.excerption.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(700)
            val nextActivity = if (SessionManager.isLoggedIn(this@SplashActivity)) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }
    }
}
