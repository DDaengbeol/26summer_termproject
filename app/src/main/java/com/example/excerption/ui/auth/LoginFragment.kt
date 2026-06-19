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
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.data.AppContainer
import com.example.excerption.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = view.findViewById<EditText>(R.id.emailEditText)
        val passwordInput = view.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val signUpButton = view.findViewById<Button>(R.id.showSignUpButton)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ⭕ 코루틴을 사용하여 DB에서 유저 정보를 조회하고 검증합니다.
            viewLifecycleOwner.lifecycleScope.launch {
                // 주의: AppContainer에 userRepository가 정의되어 있어야 합니다.
                val user = AppContainer.userRepository.getUserById(email)

                if (user != null && user.password == password) {
                    // 로그인 성공
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    // 로그인 실패 (ID 없음 또는 비번 불일치)
                    Toast.makeText(requireContext(), "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        signUpButton.setOnClickListener {
            (requireActivity() as? LoginActivity)?.navigateToSignUp()
        }
    }
}