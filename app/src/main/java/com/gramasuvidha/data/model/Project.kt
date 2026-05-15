package com.gramasuvidha.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Project data class representing a government-funded local project.
 * Used as both a Room entity (local cache) and Firestore model.
 */
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val type: String = "",          // Road, Borewell, Hall, Bridge, etc.
    val budget: Long = 0,
    val startDate: String = "",     // Format: yyyy-MM-dd
    val endDate: String = "",       // Format: yyyy-MM-dd
    val progress: Int = 0,          // 0-100 percentage
    val status: String = "Not Started",  // Not Started, In Progress, Completed
    val description: String = "",
    val beforeImageUrl: String = "",
    val afterImageUrl: String = "",
    val location: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Converts this Project to a Map for Firestore storage.
     */
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "type" to type,
            "budget" to budget,
            "start_date" to startDate,
            "end_date" to endDate,
            "progress" to progress,
            "status" to status,
            "description" to description,
            "images" to mapOf("before" to beforeImageUrl, "after" to afterImageUrl),
            "location" to location,
            "lastUpdated" to System.currentTimeMillis()
        )
    }

    companion object {
        /**
         * Creates a Project from a Firestore document map.
         */
        fun fromFirestoreMap(map: Map<String, Any>, docId: String): Project {
            val images = map["images"] as? Map<*, *>
            return Project(
                id = docId,
                name = map["name"] as? String ?: "",
                type = map["type"] as? String ?: "",
                budget = (map["budget"] as? Number)?.toLong() ?: 0,
                startDate = map["start_date"] as? String ?: "",
                endDate = map["end_date"] as? String ?: "",
                progress = (map["progress"] as? Number)?.toInt() ?: 0,
                status = map["status"] as? String ?: "Not Started",
                description = map["description"] as? String ?: "",
                beforeImageUrl = images?.get("before") as? String ?: "",
                afterImageUrl = images?.get("after") as? String ?: "",
                location = map["location"] as? String ?: "",
                lastUpdated = (map["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
