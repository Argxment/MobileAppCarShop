package com.example.pastuhovvprojectmobile

import android.content.Context

class SessionManager(context: Context) {
    private val p = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    fun saveUser(l: String) = p.edit().putString("login", l).apply()
    fun getLogin() = p.getString("login", null)
    fun logout() = p.edit().clear().apply()
}