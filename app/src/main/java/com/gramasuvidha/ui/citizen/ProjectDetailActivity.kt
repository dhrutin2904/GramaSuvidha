package com.gramasuvidha.ui.citizen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.gramasuvidha.R
import com.gramasuvidha.data.model.Project
import com.gramasuvidha.ui.viewmodel.ProjectViewModel
import com.gramasuvidha.utils.Constants
import com.gramasuvidha.utils.LocaleHelper
import com.gramasuvidha.utils.PrefsManager

/**
 * Project Detail Activity showing detailed information about a project
 * including animated progress bar, before/after images, and status badge.
 * Feedback button is hidden for guest users.
 */
class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ProjectViewModel

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvProjectName: TextView
    private lateinit var tvProjectType: TextView
    private lateinit var tvBudget: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvProgressPercent: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var chipStatus: Chip
    private lateinit var ivBefore: ImageView
    private lateinit var ivAfter: ImageView
    private lateinit var tvBeforeLabel: TextView
    private lateinit var tvAfterLabel: TextView
    private lateinit var btnFeedback: MaterialButton

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        val projectId = intent.getStringExtra(Constants.EXTRA_PROJECT_ID) ?: return finish()
        val projectName = intent.getStringExtra(Constants.EXTRA_PROJECT_NAME) ?: ""

        initViews()
        toolbar.title = projectName

        viewModel.getProjectById(projectId).observe(this) { project ->
            project?.let { bindProjectData(it) }
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        tvProjectName = findViewById(R.id.tv_project_name)
        tvProjectType = findViewById(R.id.tv_project_type)
        tvBudget = findViewById(R.id.tv_budget)
        tvStartDate = findViewById(R.id.tv_start_date)
        tvEndDate = findViewById(R.id.tv_end_date)
        tvDescription = findViewById(R.id.tv_description)
        tvLocation = findViewById(R.id.tv_location)
        tvProgressPercent = findViewById(R.id.tv_progress_percent)
        progressBar = findViewById(R.id.progress_bar_detail)
        chipStatus = findViewById(R.id.chip_status)
        ivBefore = findViewById(R.id.iv_before)
        ivAfter = findViewById(R.id.iv_after)
        tvBeforeLabel = findViewById(R.id.tv_before_label)
        tvAfterLabel = findViewById(R.id.tv_after_label)
        btnFeedback = findViewById(R.id.btn_feedback)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Hide feedback button for guest users
        if (PrefsManager.isGuest(this)) {
            btnFeedback.visibility = View.GONE
        }
    }

    private fun bindProjectData(project: Project) {
        tvProjectName.text = project.name
        // Use localized type string
        tvProjectType.text = Constants.getLocalizedType(this, project.type)
        tvBudget.text = getString(R.string.budget_format, Constants.formatBudget(project.budget))
        tvStartDate.text = getString(R.string.start_date_format, project.startDate)
        tvEndDate.text = getString(R.string.end_date_format, project.endDate)
        tvDescription.text = project.description
        tvLocation.text = getString(R.string.location_format, project.location)

        // Animated progress bar
        tvProgressPercent.text = getString(R.string.progress_format, project.progress)
        animateProgressBar(project.progress)

        // Status badge — use localized status string
        chipStatus.text = Constants.getLocalizedStatus(this, project.status)
        chipStatus.setChipBackgroundColorResource(
            when (project.status) {
                "Completed" -> R.color.status_completed
                "In Progress" -> R.color.status_in_progress
                else -> R.color.status_not_started
            }
        )

        // Before/After images
        loadImage(project.beforeImageUrl, ivBefore)
        loadImage(project.afterImageUrl, ivAfter)

        // Show/hide image sections
        tvBeforeLabel.visibility = if (project.beforeImageUrl.isNotEmpty()) View.VISIBLE else View.GONE
        ivBefore.visibility = if (project.beforeImageUrl.isNotEmpty()) View.VISIBLE else View.GONE
        tvAfterLabel.visibility = if (project.afterImageUrl.isNotEmpty()) View.VISIBLE else View.GONE
        ivAfter.visibility = if (project.afterImageUrl.isNotEmpty()) View.VISIBLE else View.GONE

        // Feedback button — only for non-guest users
        if (!PrefsManager.isGuest(this)) {
            btnFeedback.setOnClickListener {
                val intent = Intent(this, FeedbackActivity::class.java)
                intent.putExtra(Constants.EXTRA_PROJECT_ID, project.id)
                intent.putExtra(Constants.EXTRA_PROJECT_NAME, project.name)
                startActivity(intent)
            }
        }
    }

    private fun animateProgressBar(targetProgress: Int) {
        progressBar.max = 100
        val animation = android.animation.ObjectAnimator.ofInt(
            progressBar, "progress", 0, targetProgress
        )
        animation.duration = 1500
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.start()
    }

    private fun loadImage(url: String, imageView: ImageView) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(imageView)
        }
    }
}
