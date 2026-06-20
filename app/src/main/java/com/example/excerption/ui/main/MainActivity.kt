package com.example.excerption.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.excerption.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        applyBottomNavigationInsets(bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_calendar -> replaceFragment(RecordsFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
        return true
    }

    private fun applyBottomNavigationInsets(bottomNavigation: BottomNavigationView) {
        val initialBottomPadding = bottomNavigation.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = initialBottomPadding + bottomInset)
            insets
        }
        ViewCompat.requestApplyInsets(bottomNavigation)
    }
}
