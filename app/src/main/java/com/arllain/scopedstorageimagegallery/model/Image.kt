package com.arllain.scopedstorageimagegallery.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val id: Long,
    val uri: Uri,
    val name: String,
    val date: String) :
    Parcelable