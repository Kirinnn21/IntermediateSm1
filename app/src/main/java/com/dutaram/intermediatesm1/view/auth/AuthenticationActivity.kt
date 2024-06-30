package com.dutaram.intermediatesm1.view.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dutaram.intermediatesm1.databinding.ActivityAuthenticationBinding
import com.dutaram.intermediatesm1.view.login.LoginActivity
import com.dutaram.intermediatesm1.view.signup.SignupActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        setupAction()
    }

    private fun playAnimation() {
        val animator = ObjectAnimator.ofFloat(binding.ivPageIcon, "translationX", 1000f, 0f).apply {
            duration = 5000 // duration of the animation
        }
        animator.start()


        android.os.Handler().postDelayed({
            animator.cancel()
        }, 5000)

        val title = createAnimation(binding.tvAppTitle)
        val desc = createAnimation(binding.pageDescription)
        val btnLogin = createAnimation(binding.btnLogin)
        val btnSignup = createAnimation(binding.btnSignup)

        val button = AnimatorSet().apply {
            playTogether(btnLogin, btnSignup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, button)
            startDelay = 2000
            start()
        }
    }

    private fun createAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(DELAY_DURATION)
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

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    companion object {
        private const val DELAY_DURATION: Long = 500
    }

}

