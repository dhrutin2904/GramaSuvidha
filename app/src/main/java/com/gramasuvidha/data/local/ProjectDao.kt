package com.gramasuvidha.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gramasuvidha.data.model.Project

/**
 * Data Access Object for Project entity.
 * Provides methods for CRUD operations on the local Room database.
 */
@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY lastUpdated DESC")
    fun getAllProjects(): LiveData<List<Project>>

    @Query("SELECT * FROM projects ORDER BY lastUpdated DESC")
    suspend fun getAllProjectsList(): List<Project>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: String): LiveData<Project?>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectByIdSync(projectId: String): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjects(projects: List<Project>)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Query("SELECT COUNT(*) FROM projects WHERE status = :status")
    suspend fun getProjectCountByStatus(status: String): Int
}
