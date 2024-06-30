package com.dutaram.intermediatesm1.view.story


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dutaram.intermediatesm1.R
import com.dutaram.intermediatesm1.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setDetails()
        playAnimation()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun setupView() {
        val actionBar = supportActionBar
        actionBar?.title = "Story Detail"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setDetails() {
        val name = intent.getStringExtra(EXTRA_NAME)
        val date = intent.getStringExtra(EXTRA_DATE)
        val url = intent.getStringExtra(EXTRA_URL)
        val desc = intent.getStringExtra(EXTRA_DESCRIPTION)

        binding.tvDetailName.text = getString(R.string.story_by, name)
        binding.tvItemDate.text = getString(R.string.created_on, date)
        binding.tvDetailDescription.text = desc
        Glide.with(this)
            .load(url)
            .into(binding.ivItemPhoto)
    }


    private fun playAnimation() {

        val image = createAnimation(binding.ivItemPhoto)
        val author = createAnimation(binding.tvDetailName)
        val date = createAnimation(binding.tvItemDate)
        val descTitle = createAnimation(binding.tvDescriptionTitle)
        val descValue = createAnimation(binding.tvDetailDescription)

        val desc = AnimatorSet().apply {
            playTogether(descTitle, descValue)
        }

        AnimatorSet().apply {
            playSequentially(image, author, date, desc)
            startDelay = 500
            start()
        }
    }


    private fun createAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(DELAY_DURATION)
    }


    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_DATE = "date"
        const val EXTRA_URL = "url"
        const val EXTRA_DESCRIPTION = "description"
        private const val DELAY_DURATION: Long = 500
    }
}