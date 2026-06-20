package com.example.excerption.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.excerption.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.authContainer, LoginFragment())
                .commit()
        }
    }
}
