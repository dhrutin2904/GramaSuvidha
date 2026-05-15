package com.gramasuvidha.ui.citizen

import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gramasuvidha.R
import com.gramasuvidha.data.model.Feedback
import com.gramasuvidha.ui.viewmodel.ProjectViewModel
import com.gramasuvidha.utils.Constants
import com.gramasuvidha.utils.LocaleHelper
import com.gramasuvidha.utils.PrefsManager
import java.util.UUID

/**
 * Feedback Activity where citizens can:
 * - Rate a project (1-5 stars)
 * - Report issues via text
 * Feedback is submitted to Firebase Firestore.
 */
class FeedbackActivity : AppCompatActivity() {

    private lateinit var viewModel: ProjectViewModel

    private lateinit var toolbar: MaterialToolbar
    private lateinit var ratingBar: RatingBar
    private lateinit var tilIssue: TextInputLayout
    private lateinit var etIssue: TextInputEditText
    private lateinit var btnSubmit: MaterialButton

    private var projectId: String = ""
    private var projectName: String = ""

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        projectId = intent.getStringExtra(Constants.EXTRA_PROJECT_ID) ?: return finish()
        projectName = intent.getStringExtra(Constants.EXTRA_PROJECT_NAME) ?: ""

        initViews()
        observeData()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ratingBar = findViewById(R.id.rating_bar)
        tilIssue = findViewById(R.id.til_issue)
        etIssue = findViewById(R.id.et_issue)
        btnSubmit = findViewById(R.id.btn_submit_feedback)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.feedback_for, projectName)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        btnSubmit.setOnClickListener { submitFeedback() }
    }

    private fun submitFeedback() {
        val rating = ratingBar.rating
        val issueText = etIssue.text.toString().trim()

        if (rating == 0f) {
            Toast.makeText(this, getString(R.string.please_rate), Toast.LENGTH_SHORT).show()
            return
        }

        val feedback = Feedback(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            projectName = projectName,
            rating = rating,
            issueReport = issueText,
            citizenPhone = PrefsManager.getUserPhone(this),
            timestamp = System.currentTimeMillis()
        )

        viewModel.submitFeedback(feedback)
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
