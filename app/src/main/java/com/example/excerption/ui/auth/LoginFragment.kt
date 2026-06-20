package com.example.excerption.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.excerption.R
import com.example.excerption.common.SessionManager
import com.example.excerption.ui.main.MainActivity

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val showSignUpButton = view.findViewById<Button>(R.id.showSignUpButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isBlank()) {
                Toast.makeText(requireContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SessionManager.login(requireContext(), email)
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }

        showSignUpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authContainer, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
