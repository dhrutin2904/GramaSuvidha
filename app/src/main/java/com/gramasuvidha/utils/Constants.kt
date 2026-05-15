package com.gramasuvidha.utils

import android.content.Context
import com.gramasuvidha.R

/**
 * Application-wide constants.
 */
object Constants {

    // Admin hardcoded credentials (for demo purposes)
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_PASSWORD = "admin123"

    // Firebase collection names
    const val COLLECTION_PROJECTS = "projects"
    const val COLLECTION_FEEDBACK = "feedback"

    // Project types
    val PROJECT_TYPES = listOf(
        "Road",
        "Borewell",
        "Hall",
        "Bridge",
        "Electricity",
        "Education",
        "Sanitation",
        "Healthcare",
        "Irrigation",
        "Other"
    )

    // Project statuses
    val PROJECT_STATUSES = listOf(
        "Not Started",
        "In Progress",
        "Completed"
    )

    // Intent extras
    const val EXTRA_PROJECT_ID = "extra_project_id"
    const val EXTRA_PROJECT_NAME = "extra_project_name"
    const val EXTRA_IS_EDIT = "extra_is_edit"

    // Currency format
    const val CURRENCY_SYMBOL = "₹"

    /**
     * Returns the localized display string for a project status.
     * Status is stored in English in the database; this maps it to the current locale.
     */
    fun getLocalizedStatus(context: Context, status: String): String {
        return when (status) {
            "Not Started" -> context.getString(R.string.status_not_started)
            "In Progress" -> context.getString(R.string.status_in_progress)
            "Completed" -> context.getString(R.string.status_completed)
            else -> status
        }
    }

    /**
     * Returns the localized display string for a project type.
     * Type is stored in English in the database; this maps it to the current locale.
     */
    fun getLocalizedType(context: Context, type: String): String {
        return when (type) {
            "Road" -> context.getString(R.string.type_road)
            "Borewell" -> context.getString(R.string.type_borewell)
            "Hall" -> context.getString(R.string.type_hall)
            "Bridge" -> context.getString(R.string.type_bridge)
            "Electricity" -> context.getString(R.string.type_electricity)
            "Education" -> context.getString(R.string.type_education)
            "Sanitation" -> context.getString(R.string.type_sanitation)
            "Healthcare" -> context.getString(R.string.type_healthcare)
            "Irrigation" -> context.getString(R.string.type_irrigation)
            "Other" -> context.getString(R.string.type_other)
            else -> type
        }
    }

    /**
     * Formats a budget amount to Indian currency format.
     * e.g., 500000 → "₹5,00,000"
     */
    fun formatBudget(amount: Long): String {
        val amountStr = amount.toString()
        if (amountStr.length <= 3) return "$CURRENCY_SYMBOL$amountStr"

        val lastThree = amountStr.substring(amountStr.length - 3)
        val rest = amountStr.substring(0, amountStr.length - 3)
        val formatted = rest.reversed().chunked(2).joinToString(",").reversed()
        return "$CURRENCY_SYMBOL$formatted,$lastThree"
    }
}

