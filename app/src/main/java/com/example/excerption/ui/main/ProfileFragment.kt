package com.example.excerption.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.excerption.R
import com.example.excerption.common.SessionManager
import com.example.excerption.ui.auth.LoginActivity

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.profileEmailTextView).text =
            "로그인 계정\n${SessionManager.email(requireContext())}"

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            SessionManager.logout(requireContext())
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}
