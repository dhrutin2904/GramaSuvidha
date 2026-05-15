package com.gramasuvidha.data.mock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gramasuvidha.data.model.Project

/**
 * MockApiService simulates API responses using local JSON data.
 *
 * This service is used for development and testing purposes.
 * It reads project data from `assets/mock_projects.json` and returns
 * structured Project objects, mimicking a real REST API.
 *
 * ## Usage:
 * ```kotlin
 * val mockApi = MockApiService(context)
 * val projects = mockApi.getProjects()
 * val singleProject = mockApi.getProjectById("001")
 * ```
 *
 * ## JSON Structure (assets/mock_projects.json):
 * ```json
 * {
 *   "projects": [
 *     {
 *       "id": "001",
 *       "name": "Main Road Repair",
 *       "type": "Road",
 *       "budget": 500000,
 *       "start_date": "2024-01-01",
 *       "end_date": "2024-06-01",
 *       "progress": 75,
 *       "status": "In Progress",
 *       "description": "Repair of the main village road connecting to the highway.",
 *       "location": "Halasuru Village",
 *       "images": {
 *         "before": "https://example.com/road_before.jpg",
 *         "after": "https://example.com/road_after.jpg"
 *       }
 *     }
 *   ]
 * }
 * ```
 *
 * ## API Simulation Methods:
 *
 * | Method              | Returns              | Description                        |
 * |---------------------|----------------------|------------------------------------|
 * | getProjects()       | List<Project>        | All projects from JSON             |
 * | getProjectById(id)  | Project?             | Single project by ID               |
 * | getProjectsByStatus | List<Project>        | Filter by status string            |
 * | addProject(project) | Boolean              | Simulates adding (always true)     |
 * | updateProgress(id, %)| Boolean             | Simulates progress update          |
 *
 * @param context Application context for reading assets
 */
class MockApiService(private val context: Context) {

    private val gson = Gson()

    /**
     * Data wrapper class for parsing the root JSON structure.
     */
    private data class ProjectsResponse(val projects: List<MockProject>)

    /**
     * Internal mock project model matching the JSON structure.
     */
    private data class MockProject(
        val id: String,
        val name: String,
        val type: String = "",
        val budget: Long,
        val start_date: String,
        val end_date: String,
        val progress: Int,
        val status: String,
        val description: String = "",
        val location: String = "",
        val images: MockImages? = null
    )

    private data class MockImages(
        val before: String = "",
        val after: String = ""
    )

    /**
     * Reads and parses the mock JSON file from assets.
     * @return List of Project objects, or empty list if parsing fails.
     */
    fun getProjects(): List<Project> {
        return try {
            val jsonString = context.assets.open("mock_projects.json")
                .bufferedReader()
                .use { it.readText() }

            val response = gson.fromJson(jsonString, ProjectsResponse::class.java)

            response.projects.map { mock ->
                Project(
                    id = mock.id,
                    name = mock.name,
                    type = mock.type,
                    budget = mock.budget,
                    startDate = mock.start_date,
                    endDate = mock.end_date,
                    progress = mock.progress,
                    status = mock.status,
                    description = mock.description,
                    location = mock.location,
                    beforeImageUrl = mock.images?.before ?: "",
                    afterImageUrl = mock.images?.after ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Returns a single project by ID from the mock data.
     * @param id The project ID to search for.
     * @return The matching Project, or null if not found.
     */
    fun getProjectById(id: String): Project? {
        return getProjects().find { it.id == id }
    }

    /**
     * Returns projects filtered by status.
     * @param status Status string: "Not Started", "In Progress", or "Completed"
     * @return Filtered list of projects.
     */
    fun getProjectsByStatus(status: String): List<Project> {
        return getProjects().filter { it.status == status }
    }

    /**
     * Simulates adding a new project.
     * In a real implementation, this would POST to an API endpoint.
     * @param project The project to add.
     * @return Always returns true (simulation).
     */
    fun addProject(project: Project): Boolean {
        // In production: POST /api/projects
        // Body: project.toFirestoreMap()
        return true
    }

    /**
     * Simulates updating project progress.
     * In a real implementation, this would PATCH/PUT to an API endpoint.
     * @param projectId The project ID to update.
     * @param newProgress The new progress percentage (0-100).
     * @return Always returns true (simulation).
     */
    fun updateProgress(projectId: String, newProgress: Int): Boolean {
        // In production: PATCH /api/projects/{projectId}
        // Body: { "progress": newProgress }
        return true
    }
}
