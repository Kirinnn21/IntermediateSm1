package com.dutaram.intermediatesm1.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dutaram.intermediatesm1.R
import com.dutaram.intermediatesm1.ViewModelFactory
import com.dutaram.intermediatesm1.data.pref.UserPreference
import com.dutaram.intermediatesm1.databinding.ActivityAddStoryBinding
import com.dutaram.intermediatesm1.view.liststory.ListStoryActivity
import com.dutaram.intermediatesm1.view.login.LoginActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
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
                    "Aktifkan izin terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                rotateFile(myFile)
                binding.ivImagePreview.setImageURI(uri)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val newFile = File(currentPhotoPath)
            newFile.let { file ->
                getFile = file
                rotateFile(file)
                binding.ivImagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupView() {
        val actionBar = supportActionBar
        actionBar?.title = "Unggah Cerita Baru"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        ).get(AddStoryViewModel::class.java)

        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        addStoryViewModel.finishActivity.observe(this) {
            if (it == true) {
                val intent = Intent(this, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun showLoading(it: Boolean?) {

    }

    private fun setupAction() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }

        addStoryViewModel.getLocation()

        addStoryViewModel.location.observe(this) { location ->
            binding.btnUpload.setOnClickListener {
                val desc = binding.edAddDescription
                desc.error = if (desc.text.toString().isEmpty()) "Masukkan deskripsi" else null

                if (desc.error == null) {
                    if (binding.cbLocation.isChecked && location != null) {
                        uploadStory(location.latitude.toFloat(), location.longitude.toFloat())
                    } else {
                        uploadStory(null, null)
                    }
                }
            }
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.dutaram.intermediatesm1.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory(latitude: Float?, longitude: Float?) {
        if (getFile != null) {
            val file = reduceFileImage(getFile!!)
            val description = binding.edAddDescription.text.toString()
            val requestImageFile = file.asRequestBody("image/jpg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.getUser().observe(this) { user ->
                val token = "Bearer " + user.token
                if (latitude != null && longitude != null) {
                    addStoryViewModel.uploadStoryWithLocation(token, imageMultipart, description, latitude, longitude)
                } else {
                    addStoryViewModel.uploadStory(token, imageMultipart, description)
                }
            }
        } else {
            Toast.makeText(this, "Pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_story, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {

        val sharedPreferences = getSharedPreferences("User_Preference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token_key")
        editor.apply()


        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
