package com.example.excerption.common

import android.content.Context

object SessionManager {
    private const val PREF_NAME = "excerption_session"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_EMAIL = "email"

    fun login(context: Context, email: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGGED_IN, false)
    }

    fun email(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "guest@excerption.local")
            .orEmpty()
    }
}
