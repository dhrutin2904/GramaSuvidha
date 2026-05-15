package com.gramasuvidha.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gramasuvidha.data.local.ProjectDao
import com.gramasuvidha.data.mock.MockApiService
import com.gramasuvidha.data.model.Feedback
import com.gramasuvidha.data.model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository that manages project data from multiple sources:
 * 1. Room Database (local cache - primary for offline)
 * 2. Firebase Firestore (remote - syncs when online)
 * 3. MockApiService (fallback for initial seed data)
 */
class ProjectRepository(
    private val projectDao: ProjectDao,
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val projectsCollection = firestore.collection("projects")
    private val feedbackCollection = firestore.collection("feedback")
    private val mockApiService = MockApiService(context)

    /**
     * Returns LiveData of all projects from the local Room database.
     * This is the primary data source for the UI.
     */
    fun getAllProjects(): LiveData<List<Project>> {
        return projectDao.getAllProjects()
    }

    /**
     * Returns LiveData of a single project by ID.
     */
    fun getProjectById(projectId: String): LiveData<Project?> {
        return projectDao.getProjectById(projectId)
    }

    /**
     * Syncs projects from Firebase Firestore to local Room database.
     * If Firebase is unavailable and local DB is empty, seeds from MockApiService.
     */
    suspend fun syncProjects(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (isNetworkAvailable()) {
                // Try to fetch from Firebase
                val snapshot = projectsCollection
                    .orderBy("lastUpdated", Query.Direction.DESCENDING)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val projects = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { Project.fromFirestoreMap(it, doc.id) }
                    }
                    projectDao.deleteAllProjects()
                    projectDao.insertAllProjects(projects)
                    return@withContext Result.success(true)
                }
            }

            // If no network or no Firebase data, seed from mock if DB is empty
            val localCount = projectDao.getProjectCount()
            if (localCount == 0) {
                val mockProjects = mockApiService.getProjects()
                if (mockProjects.isNotEmpty()) {
                    projectDao.insertAllProjects(mockProjects)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            // If sync fails, seed from mock if local DB is empty
            val localCount = projectDao.getProjectCount()
            if (localCount == 0) {
                val mockProjects = mockApiService.getProjects()
                if (mockProjects.isNotEmpty()) {
                    projectDao.insertAllProjects(mockProjects)
                }
            }
            Result.failure(e)
        }
    }

    /**
     * Adds or updates a project in both Firestore and local cache.
     */
    suspend fun saveProject(project: Project): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Save locally first (offline-first approach)
            projectDao.insertProject(project)

            // Then try to sync to Firebase
            if (isNetworkAvailable()) {
                projectsCollection.document(project.id)
                    .set(project.toFirestoreMap())
                    .await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates the progress of a project.
     */
    suspend fun updateProjectProgress(projectId: String, progress: Int): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val project = projectDao.getProjectByIdSync(projectId)
                if (project != null) {
                    val status = when {
                        progress == 0 -> "Not Started"
                        progress == 100 -> "Completed"
                        else -> "In Progress"
                    }
                    val updated = project.copy(
                        progress = progress,
                        status = status,
                        lastUpdated = System.currentTimeMillis()
                    )
                    projectDao.updateProject(updated)

                    if (isNetworkAvailable()) {
                        projectsCollection.document(projectId)
                            .update(
                                mapOf(
                                    "progress" to progress,
                                    "status" to status,
                                    "lastUpdated" to System.currentTimeMillis()
                                )
                            )
                            .await()
                    }
                }
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Deletes a project from both local and remote storage.
     */
    suspend fun deleteProject(projectId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            projectDao.deleteProjectById(projectId)
            if (isNetworkAvailable()) {
                projectsCollection.document(projectId).delete().await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submits citizen feedback to Firebase Firestore.
     */
    suspend fun submitFeedback(feedback: Feedback): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            feedbackCollection.document(feedback.id)
                .set(feedback.toFirestoreMap())
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets dashboard statistics for the admin panel.
     */
    suspend fun getDashboardStats(): Triple<Int, Int, Int> = withContext(Dispatchers.IO) {
        val total = projectDao.getProjectCount()
        val completed = projectDao.getProjectCountByStatus("Completed")
        val inProgress = projectDao.getProjectCountByStatus("In Progress")
        Triple(total, completed, inProgress)
    }

    /**
     * Checks if network connectivity is available.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
