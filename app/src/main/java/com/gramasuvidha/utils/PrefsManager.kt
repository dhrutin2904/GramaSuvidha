package com.gramasuvidha.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user session and role preferences using SharedPreferences.
 */
object PrefsManager {

    private const val PREF_NAME = "grama_suvidha_prefs"
    private const val KEY_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_NAME = "user_name"

    // Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_CITIZEN = "citizen"
    const val ROLE_GUEST = "guest"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves user login session.
     */
    fun saveLogin(context: Context, role: String, name: String = "", phone: String = "") {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_ROLE, role)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_PHONE, phone)
            apply()
        }
    }

    /**
     * Checks if user is logged in.
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Returns the current user role.
     */
    fun getRole(context: Context): String {
        return getPrefs(context).getString(KEY_ROLE, ROLE_GUEST) ?: ROLE_GUEST
    }

    /**
     * Returns the user's name.
     */
    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "") ?: ""
    }

    /**
     * Returns the user's phone number.
     */
    fun getUserPhone(context: Context): String {
        return getPrefs(context).getString(KEY_USER_PHONE, "") ?: ""
    }

    /**
     * Checks if the current user is an admin.
     */
    fun isAdmin(context: Context): Boolean {
        return getRole(context) == ROLE_ADMIN
    }

    /**
     * Checks if the current user is a guest (browsing without login).
     */
    fun isGuest(context: Context): Boolean {
        return getRole(context) == ROLE_GUEST
    }

    /**
     * Clears the session (logout).
     */
    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
