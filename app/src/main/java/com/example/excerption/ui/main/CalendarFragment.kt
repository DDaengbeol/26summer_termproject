package com.example.excerption.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.excerption.R

// ⭕ 일반 class로 명확히 선언하고 Fragment를 상속받아야 합니다!
class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 💡 fragment_calendar 레이아웃 파일 이름이 프로젝트와 맞는지 확인해 주세요.
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}