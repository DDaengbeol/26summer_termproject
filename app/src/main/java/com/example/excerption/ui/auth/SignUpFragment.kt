package com.example.excerption.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.UserEntity
import kotlinx.coroutines.launch

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // XML에 정의된 정확한 ID로 연결
        val emailInput = view.findViewById<EditText>(R.id.signUpEmailEditText)
        val passwordInput = view.findViewById<EditText>(R.id.signUpPasswordEditText)
        val signUpButton = view.findViewById<Button>(R.id.signUpButton)
        val backToLoginButton = view.findViewById<Button>(R.id.backToLoginButton)

        // 1. 회원가입 버튼 로직
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // DB 작업은 코루틴(lifecycleScope) 내부에서 처리
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        // UserEntity의 userId 파라미터에 email 값 전달
                        val newUser = UserEntity(userId = email, password = password)
                        AppContainer.userRepository.insertUser(newUser)

                        Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. 로그인 화면으로 돌아가기 버튼 로직
        backToLoginButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}