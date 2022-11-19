package com.arvind.picsumphotoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arvind.picsumphotoapp.data.model.PageKey

@Dao
interface PageKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<PageKey>)

    @Query("SELECT * FROM pageKey WHERE id LIKE :id")
    suspend fun getNextPageKey(id: String): PageKey?

    @Query("DELETE FROM pageKey")
    suspend fun clearAll()
}