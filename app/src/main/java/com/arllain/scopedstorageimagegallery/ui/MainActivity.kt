package com.arllain.scopedstorageimagegallery.ui

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arllain.scopedstorageimagegallery.R
import com.arllain.scopedstorageimagegallery.adapter.ImageAdapter
import com.arllain.scopedstorageimagegallery.databinding.ActivityMainBinding
import com.arllain.scopedstorageimagegallery.model.Image

private const val REQUEST_PERMISSION_MEDIA_ACCESS = 100

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAIN_ACTIVITY_IMAGE_EXTRA_ID = "image"
        const val MAIN_ACTIVITY_DETAILS_REQUEST_CODE  = 1
    }

    private lateinit var binding: ActivityMainBinding

    private val imageAdapter by lazy {
        ImageAdapter { clickedImage ->
            val viewImageIntent = Intent(this@MainActivity, ImageDetailsActivity::class.java)
            viewImageIntent.putExtra(MAIN_ACTIVITY_IMAGE_EXTRA_ID, clickedImage)
            startActivityForResult(viewImageIntent, MAIN_ACTIVITY_DETAILS_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        requestPermission()
    }

    private fun initUi() {
        val rvImages: RecyclerView = findViewById(R.id.rvImages)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_space) / 2
        rvImages.apply {
            rvImages.adapter = imageAdapter
            setHasFixedSize(true)
            setPadding(spacing, spacing, spacing, spacing)
            layoutManager = GridLayoutManager(this.context,2)
        }
    }

    private fun loadImages() {
        val imageList = queryImagesOnDevice()
        imageAdapter.submitList(imageList)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_MEDIA_ACCESS,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_MEDIA_ACCESS &&
            grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, "Permission allowed", Toast.LENGTH_SHORT).show()
            initUi()
            loadImages()
        }
    }


    private fun queryImagesOnDevice(): List<Image> {
        val imageList = mutableListOf<Image>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                val date =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageList += Image(id, uri, name, date)
            }

            cursor.close()
        }

        return imageList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            loadImages()
        }
    }

}