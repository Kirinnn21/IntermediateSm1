package com.dutaram.intermediatesm1.view.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dutaram.intermediatesm1.ViewModelFactory
import com.dutaram.intermediatesm1.data.pref.UserPreference
import com.dutaram.intermediatesm1.databinding.ActivityMainBinding
import com.dutaram.intermediatesm1.view.auth.AuthenticationActivity
import com.dutaram.intermediatesm1.view.liststory.ListStoryActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupViewModel() {
        var intent: Intent
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                intent = Intent(this, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                    finish()
                }, DELAY_MILLIS)
            } else {
                intent = Intent(this, AuthenticationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                    finish()
                }, DELAY_MILLIS)
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS: Long = 2000
    }
}

