package com.arvind.picsumphotoapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arvind.picsumphotoapp.data.local.dao.PhotosImageListDao
import com.arvind.picsumphotoapp.data.local.database.PhotosImageListDb
import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.data.remote.ApiServices
import com.arvind.picsumphotoapp.paging.ImageListRemoteMediator
import com.arvind.picsumphotoapp.paging.ImagePagingDataSource
import com.arvind.picsumphotoapp.utils.Constants.QUERY_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ImageListRepositoryImpl @Inject constructor(
    private val apiServices: ApiServices,
    private val localDataSource: PhotosImageListDao,
    private val imageListDb: PhotosImageListDb
) : ImageListRepository {
    override suspend fun getImages(): Flow<PagingData<ImageListModel>> {
        val pagingSourceRemote = { ImagePagingDataSource(apiServices) }
        val pagingSourceLocal = { imageListDb.getImageListDao().getAllImages() }
        return Pager(
            config = PagingConfig(pageSize = QUERY_PER_PAGE, prefetchDistance = 2),
            remoteMediator = ImageListRemoteMediator(apiServices, imageListDb),
            pagingSourceFactory = pagingSourceLocal
        ).flow
    }

    override suspend fun saveImageItem(item: ImageListModel) = localDataSource.insert(item)

    override suspend fun getSavedImages() = localDataSource.getAllImages()

    override suspend fun getSavedImagesList() = localDataSource.getAllImagesList()

    override suspend fun deleteAllImages() = localDataSource.deleteAllImages()
}