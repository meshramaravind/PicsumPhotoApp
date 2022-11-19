package com.arvind.picsumphotoapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.data.remote.ApiServices
import com.arvind.picsumphotoapp.utils.Constants.TOTAL_PAGES

class ImagePagingDataSource(private val apiServices: ApiServices) :
    PagingSource<Int, ImageListModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageListModel> {
        val pageNumber = params.key ?: 1
        return try {
            val response = apiServices.getImages(pageNumber)
            val pagedResponse = response.body()

            var nextPageNumber: Int? = null
            if (pageNumber < TOTAL_PAGES) {
                nextPageNumber = pageNumber + 1
            }

            LoadResult.Page(
                data = pagedResponse.orEmpty(),
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageListModel>): Int = 1
}