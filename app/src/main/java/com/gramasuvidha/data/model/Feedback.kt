package com.gramasuvidha.data.model

/**
 * Feedback data class representing citizen feedback for a project.
 * Stored in Firebase Firestore under "feedback" collection.
 */
data class Feedback(
    val id: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val rating: Float = 0f,         // 1-5 star rating
    val issueReport: String = "",   // Text description of issue
    val citizenPhone: String = "",  // Phone of the citizen who submitted
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Converts this Feedback to a Map for Firestore storage.
     */
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "projectId" to projectId,
            "projectName" to projectName,
            "rating" to rating,
            "issueReport" to issueReport,
            "citizenPhone" to citizenPhone,
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromFirestoreMap(map: Map<String, Any>, docId: String): Feedback {
            return Feedback(
                id = docId,
                projectId = map["projectId"] as? String ?: "",
                projectName = map["projectName"] as? String ?: "",
                rating = (map["rating"] as? Number)?.toFloat() ?: 0f,
                issueReport = map["issueReport"] as? String ?: "",
                citizenPhone = map["citizenPhone"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0
            )
        }
    }
}
