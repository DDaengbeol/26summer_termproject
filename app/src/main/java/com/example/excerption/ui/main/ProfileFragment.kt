package com.example.excerption.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // 로그아웃 버튼용 임포트 추가
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.excerption.R

class ProfileFragment : Fragment() {

    // ⭕ [아이디 일치화] XML에 적힌 진짜 ID인 profileEmailTextView로 변수명을 통일합니다.
    private var profileEmailTextView: TextView? = null
    private var logoutButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ⭕ [정밀 타격] XML의 android:id="@+id/profileEmailTextView"와 정확하게 매칭합니다!
        profileEmailTextView = view.findViewById<TextView>(R.id.profileEmailTextView)
        logoutButton = view.findViewById<Button>(R.id.logoutButton)

        // 💡 [27번 줄 에러 완벽 해결 Zone]
        // 이제 아래와 같이 전역 변수를 안전하게 호출하여 데이터를 세팅하거나 리스너를 달 수 있습니다.
        // profileEmailTextView?.text = "user@example.com"

        logoutButton?.setOnClickListener {
            // 로그아웃 버튼 클릭 시 동작할 코드를 여기에 작성하시면 됩니다.
        }
    }
}