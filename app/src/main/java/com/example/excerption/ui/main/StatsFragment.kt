package com.example.excerption.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.excerption.R

// ⭕ interface가 아닌 진짜 'class'로 선언하고 Fragment를 상속받아야 합니다!
class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 💡 fragment_stats 레이아웃 파일 이름이 맞는지 확인해 주세요.
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }
}