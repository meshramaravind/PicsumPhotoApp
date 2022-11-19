package com.arvind.picsumphotoapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Entity(tableName = "photos_image_list")
@Parcelize
data class ImageListModel(
    @PrimaryKey()
    val id: String = "1",
    val author: String,
    val download_url: String?,
    val height: Int?,
    val url: String?,
    val width: Int?,
    var page: Int?
) : Parcelable
