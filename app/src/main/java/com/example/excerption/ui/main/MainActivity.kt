package com.example.excerption.ui.main // ⭕ [확인 완료] 사진 속 실제 위치인 ui.main으로 고정합니다.

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.excerption.R
import com.google.android.material.bottomnavigation.BottomNavigationView

// ❌ [중요] 기존에 있던 import com.example.excerption.ui.HomeFragment 등은 전부 삭제했습니다.
// ⭕ 같은 ui.main 폴더 안에 살고 있으므로 import문 없이 바로 호출해야 컴파일러가 알아듣습니다!

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            // ⭕ 이제 같은 패키지 내에서 HomeFragment를 올바르게 직접 참조합니다.
            replaceFragment(HomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_stats -> {
                    replaceFragment(StatsFragment())
                    true
                }
                R.id.nav_calendar -> {
                    replaceFragment(CalendarFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }
}