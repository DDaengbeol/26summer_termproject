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
                // ⭕ XML에 적힌 진짜 ID인 authContainer로 완벽 매칭!
                .replace(R.id.authContainer, LoginFragment())
                .commit()
        }
    }

    fun navigateToSignUp() {
        supportFragmentManager.beginTransaction()
            // ⭕ 여기도 똑같이 authContainer로 매칭!
            .replace(R.id.authContainer, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }
}