package com.arvind.picsumphotoapp.di

import android.content.Context
import com.arvind.picsumphotoapp.BuildConfig
import com.arvind.picsumphotoapp.data.local.dao.PhotosImageListDao
import com.arvind.picsumphotoapp.data.local.database.PhotosImageListDb
import com.arvind.picsumphotoapp.data.remote.ApiServices
import com.arvind.picsumphotoapp.data.repository.ImageListRepositoryImpl
import com.arvind.picsumphotoapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun providesBaseUrl(): String {
        return BASE_URL
    }


    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @Provides
    fun providesOkhttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        return okHttpClient.build()
    }


    @Provides
    fun providesConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }


    @Provides
    fun providesRetrofit(
        baseUrl: String,
        converterFactory: Converter.Factory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
    }


    @Provides
    fun providesRetrofitService(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }

    @Singleton
    @Provides
    fun provideImageListDb(@ApplicationContext appContext: Context) =
        PhotosImageListDb.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideImageListDao(db: PhotosImageListDb) = db.getImageListDao()

    @Singleton
    @Provides
    fun provideRepository(
        apiServices: ApiServices,
        localDataSource: PhotosImageListDao,
        imageListDb: PhotosImageListDb
    ) = ImageListRepositoryImpl(apiServices, localDataSource, imageListDb)

}