package nz.ac.uclive.ajs418.quickfire.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nz.ac.uclive.ajs418.quickfire.entity.Media

@Dao
interface MediaDao {

    @Insert
    suspend fun insert(media : Media): Long

    @Query("SELECT * FROM media")
    fun getAll(): Flow<List<Media>>

}