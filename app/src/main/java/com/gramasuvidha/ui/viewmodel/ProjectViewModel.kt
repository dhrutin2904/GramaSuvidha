package com.gramasuvidha.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.gramasuvidha.GramaSuvidhaApp
import com.gramasuvidha.data.model.Feedback
import com.gramasuvidha.data.model.Project
import kotlinx.coroutines.launch

/**
 * ViewModel for managing project data across Citizen and Admin screens.
 * Uses MVVM pattern with LiveData for reactive UI updates.
 */
class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as GramaSuvidhaApp).repository

    // All projects from Room (LiveData - automatically updated)
    val allProjects: LiveData<List<Project>> = repository.getAllProjects()

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Success messages
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    // Dashboard stats: (total, completed, inProgress)
    private val _dashboardStats = MutableLiveData<Triple<Int, Int, Int>>()
    val dashboardStats: LiveData<Triple<Int, Int, Int>> = _dashboardStats

    // Selected project for detail view
    private val _selectedProject = MutableLiveData<Project?>()
    val selectedProject: LiveData<Project?> = _selectedProject

    init {
        syncProjects()
    }

    /**
     * Syncs projects from Firebase/Mock to local database.
     */
    fun syncProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.syncProjects()
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value = "Working offline. Showing cached data."
            }
        }
    }

    /**
     * Gets a project by ID as LiveData.
     */
    fun getProjectById(projectId: String): LiveData<Project?> {
        return repository.getProjectById(projectId)
    }

    /**
     * Loads a specific project into selectedProject.
     */
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            repository.getProjectById(projectId).observeForever { project ->
                _selectedProject.value = project
            }
        }
    }

    /**
     * Saves a new or updated project.
     */
    fun saveProject(project: Project) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.saveProject(project)
            _isLoading.value = false
            if (result.isSuccess) {
                _successMessage.value = "Project saved successfully!"
            } else {
                _errorMessage.value = "Failed to save project. Please try again."
            }
        }
    }

    /**
     * Updates project progress percentage.
     */
    fun updateProgress(projectId: String, progress: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateProjectProgress(projectId, progress)
            _isLoading.value = false
            if (result.isSuccess) {
                _successMessage.value = "Progress updated!"
            } else {
                _errorMessage.value = "Failed to update progress."
            }
        }
    }

    /**
     * Deletes a project.
     */
    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteProject(projectId)
            _isLoading.value = false
            if (result.isSuccess) {
                _successMessage.value = "Project deleted."
            } else {
                _errorMessage.value = "Failed to delete project."
            }
        }
    }

    /**
     * Submits citizen feedback.
     */
    fun submitFeedback(feedback: Feedback) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.submitFeedback(feedback)
            _isLoading.value = false
            if (result.isSuccess) {
                _successMessage.value = "Feedback submitted! Thank you."
            } else {
                _errorMessage.value = "Failed to submit feedback. Please check your connection."
            }
        }
    }

    /**
     * Loads dashboard statistics.
     */
    fun loadDashboardStats() {
        viewModelScope.launch {
            val stats = repository.getDashboardStats()
            _dashboardStats.value = stats
        }
    }

    /**
     * Clears the error message after it's been shown.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Clears the success message after it's been shown.
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
