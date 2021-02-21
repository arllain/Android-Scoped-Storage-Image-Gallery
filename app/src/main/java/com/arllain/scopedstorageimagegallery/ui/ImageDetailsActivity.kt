package com.arllain.scopedstorageimagegallery.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arllain.scopedstorageimagegallery.databinding.ActivityImageDetailsBinding
import com.arllain.scopedstorageimagegallery.model.Image

class ImageDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDetailsBinding
    private var image: Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        image = intent.getParcelableExtra<Image>(MainActivity.MAIN_ACTIVITY_IMAGE_EXTRA_ID)
        binding.imageDetails.setImageURI(image?.uri)
        supportActionBar?.setTitle(image?.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}