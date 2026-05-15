package com.gramasuvidha.ui.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gramasuvidha.R
import com.gramasuvidha.data.model.Project
import com.gramasuvidha.ui.viewmodel.ProjectViewModel
import com.gramasuvidha.utils.Constants
import com.gramasuvidha.utils.LocaleHelper
import java.util.UUID

/**
 * Add/Edit Project Activity for Admin panel.
 * Allows creating new projects or editing existing ones.
 * Includes a progress slider for quick progress updates.
 */
class AddEditProjectActivity : AppCompatActivity() {

    private lateinit var viewModel: ProjectViewModel

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tilName: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var tilType: TextInputLayout
    private lateinit var spinnerType: AutoCompleteTextView
    private lateinit var tilBudget: TextInputLayout
    private lateinit var etBudget: TextInputEditText
    private lateinit var tilStartDate: TextInputLayout
    private lateinit var etStartDate: TextInputEditText
    private lateinit var tilEndDate: TextInputLayout
    private lateinit var etEndDate: TextInputEditText
    private lateinit var tilDescription: TextInputLayout
    private lateinit var etDescription: TextInputEditText
    private lateinit var tilLocation: TextInputLayout
    private lateinit var etLocation: TextInputEditText
    private lateinit var tilBeforeImage: TextInputLayout
    private lateinit var etBeforeImage: TextInputEditText
    private lateinit var tilAfterImage: TextInputLayout
    private lateinit var etAfterImage: TextInputEditText
    private lateinit var sliderProgress: Slider
    private lateinit var btnSave: MaterialButton

    private var isEdit = false
    private var editProjectId = ""

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_project)

        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        isEdit = intent.getBooleanExtra(Constants.EXTRA_IS_EDIT, false)
        editProjectId = intent.getStringExtra(Constants.EXTRA_PROJECT_ID) ?: ""

        initViews()
        setupTypeDropdown()
        observeData()

        if (isEdit && editProjectId.isNotEmpty()) {
            toolbar.title = getString(R.string.edit_project)
            loadProjectData()
        } else {
            toolbar.title = getString(R.string.add_project)
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        tilName = findViewById(R.id.til_name)
        etName = findViewById(R.id.et_name)
        tilType = findViewById(R.id.til_type)
        spinnerType = findViewById(R.id.spinner_type)
        tilBudget = findViewById(R.id.til_budget)
        etBudget = findViewById(R.id.et_budget)
        tilStartDate = findViewById(R.id.til_start_date)
        etStartDate = findViewById(R.id.et_start_date)
        tilEndDate = findViewById(R.id.til_end_date)
        etEndDate = findViewById(R.id.et_end_date)
        tilDescription = findViewById(R.id.til_description)
        etDescription = findViewById(R.id.et_description)
        tilLocation = findViewById(R.id.til_location)
        etLocation = findViewById(R.id.et_location)
        tilBeforeImage = findViewById(R.id.til_before_image)
        etBeforeImage = findViewById(R.id.et_before_image)
        tilAfterImage = findViewById(R.id.til_after_image)
        etAfterImage = findViewById(R.id.et_after_image)
        sliderProgress = findViewById(R.id.slider_progress)
        btnSave = findViewById(R.id.btn_save)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        btnSave.setOnClickListener { saveProject() }
    }

    private fun setupTypeDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            Constants.PROJECT_TYPES
        )
        spinnerType.setAdapter(adapter)
    }

    private fun loadProjectData() {
        viewModel.getProjectById(editProjectId).observe(this) { project ->
            project?.let {
                etName.setText(it.name)
                spinnerType.setText(it.type, false)
                etBudget.setText(it.budget.toString())
                etStartDate.setText(it.startDate)
                etEndDate.setText(it.endDate)
                etDescription.setText(it.description)
                etLocation.setText(it.location)
                etBeforeImage.setText(it.beforeImageUrl)
                etAfterImage.setText(it.afterImageUrl)
                sliderProgress.value = it.progress.toFloat()
            }
        }
    }

    private fun saveProject() {
        val name = etName.text.toString().trim()
        val type = spinnerType.text.toString().trim()
        val budgetStr = etBudget.text.toString().trim()
        val startDate = etStartDate.text.toString().trim()
        val endDate = etEndDate.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val beforeImage = etBeforeImage.text.toString().trim()
        val afterImage = etAfterImage.text.toString().trim()
        val progress = sliderProgress.value.toInt()

        // Validation
        var isValid = true
        if (name.isEmpty()) {
            tilName.error = getString(R.string.error_required)
            isValid = false
        } else tilName.error = null

        if (type.isEmpty()) {
            tilType.error = getString(R.string.error_required)
            isValid = false
        } else tilType.error = null

        if (budgetStr.isEmpty()) {
            tilBudget.error = getString(R.string.error_required)
            isValid = false
        } else tilBudget.error = null

        if (startDate.isEmpty()) {
            tilStartDate.error = getString(R.string.error_required)
            isValid = false
        } else tilStartDate.error = null

        if (endDate.isEmpty()) {
            tilEndDate.error = getString(R.string.error_required)
            isValid = false
        } else tilEndDate.error = null

        if (!isValid) return

        val status = when {
            progress == 0 -> "Not Started"
            progress == 100 -> "Completed"
            else -> "In Progress"
        }

        val project = Project(
            id = if (isEdit) editProjectId else UUID.randomUUID().toString(),
            name = name,
            type = type,
            budget = budgetStr.toLongOrNull() ?: 0,
            startDate = startDate,
            endDate = endDate,
            progress = progress,
            status = status,
            description = description,
            location = location,
            beforeImageUrl = beforeImage,
            afterImageUrl = afterImage,
            lastUpdated = System.currentTimeMillis()
        )

        viewModel.saveProject(project)
    }

    private fun observeData() {
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }
}
