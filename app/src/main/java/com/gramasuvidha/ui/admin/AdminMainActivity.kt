package com.gramasuvidha.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gramasuvidha.R
import com.gramasuvidha.ui.adapter.ProjectAdapter
import com.gramasuvidha.ui.auth.LoginActivity
import com.gramasuvidha.ui.citizen.ProjectDetailActivity
import com.gramasuvidha.ui.viewmodel.ProjectViewModel
import com.gramasuvidha.utils.Constants
import com.gramasuvidha.utils.LocaleHelper
import com.gramasuvidha.utils.PrefsManager

/**
 * Admin Dashboard Activity showing:
 * - Summary statistics (total, completed, in-progress)
 * - All projects with edit/delete options
 * - FAB to add new projects
 */
class AdminMainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProjectViewModel
    private lateinit var adapter: ProjectAdapter

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvTotalProjects: TextView
    private lateinit var tvCompleted: TextView
    private lateinit var tvInProgress: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvEmpty: TextView
    private lateinit var btnLanguage: MaterialButton

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        initViews()
        setupRecyclerView()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardStats()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        tvTotalProjects = findViewById(R.id.tv_total_projects)
        tvCompleted = findViewById(R.id.tv_completed)
        tvInProgress = findViewById(R.id.tv_in_progress)
        recyclerView = findViewById(R.id.rv_projects)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        fabAdd = findViewById(R.id.fab_add_project)
        tvEmpty = findViewById(R.id.tv_empty)
        btnLanguage = findViewById(R.id.btn_language)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.admin_dashboard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Back navigation — logout and go to login
        toolbar.setNavigationOnClickListener {
            PrefsManager.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Language toggle
        val currentLang = LocaleHelper.getLanguage(this)
        btnLanguage.text = if (currentLang == "en") "ಕನ್ನಡ" else "English"
        btnLanguage.setOnClickListener {
            LocaleHelper.toggleLanguage(this)
            recreate()
        }

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener {
            viewModel.syncProjects()
            viewModel.loadDashboardStats()
        }

        // FAB - Add new project
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditProjectActivity::class.java)
            intent.putExtra(Constants.EXTRA_IS_EDIT, false)
            startActivity(intent)
        }

        // Logout
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    PrefsManager.logout(this)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ProjectAdapter(
            isAdmin = true,
            onItemClick = { project ->
                val intent = Intent(this, ProjectDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PROJECT_ID, project.id)
                intent.putExtra(Constants.EXTRA_PROJECT_NAME, project.name)
                startActivity(intent)
            },
            onEditClick = { project ->
                val intent = Intent(this, AddEditProjectActivity::class.java)
                intent.putExtra(Constants.EXTRA_IS_EDIT, true)
                intent.putExtra(Constants.EXTRA_PROJECT_ID, project.id)
                startActivity(intent)
            },
            onDeleteClick = { project ->
                showDeleteConfirmation(project.id, project.name)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.allProjects.observe(this) { projects ->
            adapter.submitList(projects)
            tvEmpty.visibility = if (projects.isNullOrEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (projects.isNullOrEmpty()) View.GONE else View.VISIBLE
            viewModel.loadDashboardStats()
        }

        viewModel.dashboardStats.observe(this) { stats ->
            tvTotalProjects.text = stats.first.toString()
            tvCompleted.text = stats.second.toString()
            tvInProgress.text = stats.third.toString()
        }

        viewModel.isLoading.observe(this) { loading ->
            swipeRefresh.isRefreshing = loading
        }

        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun showDeleteConfirmation(projectId: String, projectName: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_project))
            .setMessage(getString(R.string.delete_confirmation, projectName))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteProject(projectId)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
