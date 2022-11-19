package com.arvind.picsumphotoapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arvind.picsumphotoapp.data.model.ImageListModel

@Dao
interface PhotosImageListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(imageItem: ImageListModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ImageListModel>)

    @Query("SELECT * FROM photos_image_list")
    fun getAllImages(): PagingSource<Int, ImageListModel>

    @Query("SELECT * FROM photos_image_list")
    fun getAllImagesList(): List<ImageListModel>

    @Query("Delete FROM photos_image_list")
    suspend fun deleteAllImages()
}