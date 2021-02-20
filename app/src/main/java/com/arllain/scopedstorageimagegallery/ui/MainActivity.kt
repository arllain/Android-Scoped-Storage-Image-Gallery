package com.arllain.scopedstorageimagegallery.ui

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arllain.scopedstorageimagegallery.R
import com.arllain.scopedstorageimagegallery.adapter.ImageAdapter
import com.arllain.scopedstorageimagegallery.databinding.ActivityMainBinding
import com.arllain.scopedstorageimagegallery.model.Image

private const val REQUEST_PERMISSION_MEDIA_ACCESS = 100

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var permissionDenied = false
    private val imageAdapter by lazy {
        ImageAdapter()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        requestPermission()
        initUi()
        loadImages()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUi() {
        val rvImages: RecyclerView = findViewById(R.id.rvImages)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_space) / 2
        rvImages.apply {
            rvImages.adapter = imageAdapter
            setHasFixedSize(true)
            setPadding(spacing, spacing, spacing, spacing)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(rect: Rect,
                                            view: View,
                                            parent: RecyclerView,
                                            state: RecyclerView.State) {
                    rect.set(spacing, spacing, spacing, spacing)
                }
            })

            layoutManager = GridLayoutManager(this.context,
                2)
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

        }
    }


    private fun queryImagesOnDevice(): List<Image> {
        val imageList = mutableListOf<Image>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
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
                val path =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH))
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
}