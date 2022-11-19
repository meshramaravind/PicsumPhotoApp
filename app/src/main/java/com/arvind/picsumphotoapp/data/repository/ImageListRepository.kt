package com.arvind.picsumphotoapp.data.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.arvind.picsumphotoapp.data.model.ImageListModel
import kotlinx.coroutines.flow.Flow

interface ImageListRepository {
    suspend fun getImages(): Flow<PagingData<ImageListModel>>
    suspend fun saveImageItem(item: ImageListModel): Long
    suspend fun getSavedImages(): PagingSource<Int, ImageListModel>
    suspend fun getSavedImagesList(): List<ImageListModel>
    suspend fun deleteAllImages()
}