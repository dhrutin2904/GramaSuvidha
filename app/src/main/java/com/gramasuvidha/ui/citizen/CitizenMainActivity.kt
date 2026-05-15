package com.gramasuvidha.ui.citizen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.gramasuvidha.R
import com.gramasuvidha.data.model.Project
import com.gramasuvidha.ui.adapter.ProjectAdapter
import com.gramasuvidha.ui.auth.LoginActivity
import com.gramasuvidha.ui.viewmodel.ProjectViewModel
import com.gramasuvidha.utils.Constants
import com.gramasuvidha.utils.LocaleHelper
import com.gramasuvidha.utils.PrefsManager
import java.util.Locale

/**
 * Main Citizen Activity showing the list of government-funded projects.
 * Projects are displayed as cards with progress bars.
 * Supports location-based filtering (manual entry or GPS).
 */
class CitizenMainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProjectViewModel
    private lateinit var adapter: ProjectAdapter

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var tvEmpty: TextView
    private lateinit var btnLanguage: MaterialButton

    // Location filter views
    private lateinit var etLocationFilter: TextInputEditText
    private lateinit var btnUseMyLocation: MaterialButton
    private lateinit var btnClearFilter: ImageButton
    private lateinit var tvFilterStatus: TextView

    // Location client
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // All projects (unfiltered)
    private var allProjects: List<Project> = emptyList()
    private var currentLocationFilter: String = ""

    // Permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchCurrentLocation()
        } else {
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citizen_main)

        viewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        setupRecyclerView()
        setupLocationFilter()
        observeData()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.rv_projects)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        tvEmpty = findViewById(R.id.tv_empty)
        btnLanguage = findViewById(R.id.btn_language)

        // Location filter views
        etLocationFilter = findViewById(R.id.et_location_filter)
        btnUseMyLocation = findViewById(R.id.btn_use_my_location)
        btnClearFilter = findViewById(R.id.btn_clear_filter)
        tvFilterStatus = findViewById(R.id.tv_filter_status)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.citizen_panel)
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
        }

        // Logout from toolbar menu
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

    private fun setupLocationFilter() {
        // Manual location search — on keyboard "search" action
        etLocationFilter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etLocationFilter.text.toString().trim()
                if (query.isNotEmpty()) {
                    applyLocationFilter(query)
                } else {
                    clearLocationFilter()
                }
                true
            } else {
                false
            }
        }

        // Use My Location button
        btnUseMyLocation.setOnClickListener {
            requestLocationAndFilter()
        }

        // Clear filter button
        btnClearFilter.setOnClickListener {
            clearLocationFilter()
        }
    }

    private fun requestLocationAndFilter() {
        val hasFine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchCurrentLocation() {
        Toast.makeText(this, getString(R.string.fetching_location), Toast.LENGTH_SHORT).show()

        try {
            val cancellationToken = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    reverseGeocodeAndFilter(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun reverseGeocodeAndFilter(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // Try to get locality, sub-locality, or sub-admin area
                val locationName = address.subLocality
                    ?: address.locality
                    ?: address.subAdminArea
                    ?: address.adminArea
                    ?: ""

                if (locationName.isNotEmpty()) {
                    etLocationFilter.setText(locationName)
                    applyLocationFilter(locationName)
                } else {
                    Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyLocationFilter(query: String) {
        currentLocationFilter = query
        btnClearFilter.visibility = View.VISIBLE
        tvFilterStatus.visibility = View.VISIBLE
        tvFilterStatus.text = getString(R.string.showing_projects_for, query)
        filterAndDisplayProjects()
    }

    private fun clearLocationFilter() {
        currentLocationFilter = ""
        etLocationFilter.setText("")
        btnClearFilter.visibility = View.GONE
        tvFilterStatus.visibility = View.GONE
        filterAndDisplayProjects()
    }

    private fun filterAndDisplayProjects() {
        val filtered = if (currentLocationFilter.isEmpty()) {
            allProjects
        } else {
            allProjects.filter { project ->
                project.location.contains(currentLocationFilter, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)

        if (filtered.isEmpty() && currentLocationFilter.isNotEmpty()) {
            tvEmpty.text = getString(R.string.no_projects_at_location)
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else if (filtered.isEmpty()) {
            tvEmpty.text = getString(R.string.no_projects)
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = ProjectAdapter(
            isAdmin = false,
            onItemClick = { project ->
                val intent = Intent(this, ProjectDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PROJECT_ID, project.id)
                intent.putExtra(Constants.EXTRA_PROJECT_NAME, project.name)
                startActivity(intent)
            },
            onEditClick = null,
            onDeleteClick = null
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.allProjects.observe(this) { projects ->
            allProjects = projects ?: emptyList()
            filterAndDisplayProjects()
        }

        viewModel.isLoading.observe(this) { loading ->
            swipeRefresh.isRefreshing = loading
        }
    }
}
