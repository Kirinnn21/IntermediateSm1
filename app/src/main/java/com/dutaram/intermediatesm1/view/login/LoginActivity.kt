package com.dutaram.intermediatesm1.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dutaram.intermediatesm1.ViewModelFactory
import com.dutaram.intermediatesm1.data.pref.UserLoginModel
import com.dutaram.intermediatesm1.data.pref.UserModel
import com.dutaram.intermediatesm1.data.pref.UserPreference
import com.dutaram.intermediatesm1.databinding.ActivityLoginBinding
import com.dutaram.intermediatesm1.view.liststory.ListStoryActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
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
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.finishActivity.observe(this) {
            if (it == true) {
                val intent = Intent(this, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail
            val password = binding.edLoginPassword

            if (email.text.toString().isEmpty())
                email.error = "Enter your email!"

            if (password.text.toString().isEmpty()){
                password.error = "Enter your password!"
            }

            if (email.error == null && password.error == null) {
                val user = UserLoginModel(email.text.toString(), password.text.toString())
                loginViewModel.loginUser(user, this)
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        val emailLayout = binding.editTextLayoutEmail
        val passwordLayout = binding.editTextLayoutPassword
        val progressBar = binding.progressBar
        val loginButton = binding.btnLogin

        if (isLoading) {
            emailLayout.isEnabled = false
            passwordLayout.isEnabled = false
            loginButton.isEnabled = false
            progressBar.visibility = View.VISIBLE
        } else {
            emailLayout.isEnabled = true
            passwordLayout.isEnabled = true
            loginButton.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    private fun playAnimation() {
        val imageView = binding.imageView2
        val imageViewAnimation = ObjectAnimator.ofFloat(imageView, "translationX", 1000f, 0f).apply {
            duration = 3000
            repeatMode = ObjectAnimator.REVERSE
        }

        val title = createAnimation(binding.tvTitle)
        val desc = createAnimation(binding.tvDescription)
        val emailTitle = createAnimation(binding.tvEmailTitle)
        val emailLayout = createAnimation(binding.editTextLayoutEmail)
        val emailEdit = createAnimation(binding.edLoginEmail)
        val passTitle = createAnimation(binding.tvPasswordTitle)
        val passLayout = createAnimation(binding.editTextLayoutPassword)
        val passEdit = createAnimation(binding.edLoginPassword)
        val button = createAnimation(binding.btnLogin)

        val email = AnimatorSet().apply {
            playTogether(emailTitle, emailLayout, emailEdit)
        }

        val password = AnimatorSet().apply {
            playTogether(passTitle, passLayout, passEdit)
        }

        AnimatorSet().apply {
            playSequentially(imageViewAnimation, title, desc, email, password, button)
            startDelay = DELAY_DURATION
            start()
        }
    }


    private fun createAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(DELAY_DURATION)
    }

    companion object {
        private const val DELAY_DURATION: Long = 500
    }
}
