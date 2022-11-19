package com.arvind.picsumphotoapp.data.remote

import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.utils.Constants.QUERY_PER_PAGE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("v2/list")
    suspend fun getImages(
        @Query("page")
        pageNumber: Int = 1,
        @Query("limit")
        pageSize: Int = QUERY_PER_PAGE
    ): Response<List<ImageListModel>>
}