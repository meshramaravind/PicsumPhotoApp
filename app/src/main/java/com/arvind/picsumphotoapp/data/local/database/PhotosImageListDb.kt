package com.arvind.picsumphotoapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arvind.picsumphotoapp.data.local.dao.PageKeyDao
import com.arvind.picsumphotoapp.data.local.dao.PhotosImageListDao
import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.data.model.PageKey

@Database(
    entities = [ImageListModel::class, PageKey::class],
    version = 2, exportSchema = false
)
abstract class PhotosImageListDb : RoomDatabase() {
    abstract fun getImageListDao(): PhotosImageListDao
    abstract fun pageKeyDao(): PageKeyDao

    companion object {
        @Volatile
        private var instance: PhotosImageListDb? = null

        fun getDatabase(context: Context): PhotosImageListDb =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        //Build a local database to store data
        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, PhotosImageListDb::class.java, "picsum_db")
                .fallbackToDestructiveMigration()
                .build()
    }
}