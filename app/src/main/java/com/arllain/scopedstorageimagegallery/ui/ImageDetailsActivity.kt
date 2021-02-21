package com.arllain.scopedstorageimagegallery.ui

import android.app.Activity
import android.app.RecoverableSecurityException
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.arllain.scopedstorageimagegallery.databinding.ActivityImageDetailsBinding
import com.arllain.scopedstorageimagegallery.model.Image
import android.content.Intent

private const val REQUEST_PERMISSION_DELETE = 100

class ImageDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDetailsBinding
    private var image: Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        setupDeleteButton()
    }

    private fun initUi() {
        image = intent.getParcelableExtra<Image>(MainActivity.MAIN_ACTIVITY_IMAGE_EXTRA_ID)
        binding.imageDetails.setImageURI(image?.uri)
        supportActionBar?.title = image?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupDeleteButton() {
        binding.btDeleteImage.setOnClickListener {
            try {
                image?.uri?.let { it ->
                    contentResolver.delete(
                        it, "${MediaStore.Images.Media._ID} = ?",
                        arrayOf(image?.id.toString()))
                }
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } catch (securityException: SecurityException) {
                val recoverSecException =
                securityException as? RecoverableSecurityException ?: throw
                securityException
                val intentSender = recoverSecException.userAction.actionIntent.intentSender
                startIntentSenderForResult(intentSender, 99, null, 0,
                    0, 0, null)
            }
        }
    }
}