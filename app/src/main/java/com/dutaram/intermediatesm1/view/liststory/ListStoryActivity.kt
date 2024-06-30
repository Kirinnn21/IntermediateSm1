package com.dutaram.intermediatesm1.view.liststory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dutaram.intermediatesm1.R
import com.dutaram.intermediatesm1.ViewModelFactory
import com.dutaram.intermediatesm1.adapter.LoadingStateAdapter
import com.dutaram.intermediatesm1.adapter.StoryAdapter
import com.dutaram.intermediatesm1.data.pref.UserPreference
import com.dutaram.intermediatesm1.databinding.ActivityListStoryBinding
import com.dutaram.intermediatesm1.view.auth.AuthenticationActivity
import com.dutaram.intermediatesm1.view.map.MapsActivity
import com.dutaram.intermediatesm1.view.story.AddStoryActivity
import com.dutaram.intermediatesm1.view.story.DetailStoryActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding
    private lateinit var listStoryViewModel: ListStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
        setupViewModel()
        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map -> {
                // Buka MapActivity
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Logout Confirmation")
                    setMessage("Are you sure you want to log out?")
                    setPositiveButton("yes") { _, _ ->
                        listStoryViewModel.logout()
                        val intent = Intent(context, AuthenticationActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton("No") { _, _ ->
                    }
                    create()
                    show()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirm Exit")
            setMessage("Are you sure you want to exit from the app?")
            setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
                finishAffinity()
            }
            setNegativeButton("No") { _, _ ->
            }
            create()
            show()
        }
    }

    private fun setupView() {
        val recyclerView = binding.rvListStory
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun setupViewModel() {
        listStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[ListStoryViewModel::class.java]

        listStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        setStoriesData()
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.fabAdd.setOnClickListener { view ->
            if (view.id == R.id.fab_add) {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setStoriesData() {
        val adapter = StoryAdapter()
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        listStoryViewModel.getUser()?.observe(this) { user ->
            listStoryViewModel.setToken("Bearer " + user.token)
            listStoryViewModel.getAllStories().observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }

        adapter.onItemClick = {
            val intent = Intent(this, DetailStoryActivity::class.java)
            intent.putExtra("name", it.name)
            intent.putExtra("url", it.photoUrl)
            intent.putExtra("description", it.description)
            intent.putExtra("date", it.createdAt.take(10))
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    "Turn on all permission first, including precise location.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
