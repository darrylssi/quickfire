package nz.ac.uclive.ajs418.quickfire.dao

import nz.ac.uclive.ajs418.quickfire.entity.Like
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {

    @Insert
    suspend fun insert(like : Like): Long

    @Query("SELECT * FROM like_table")
    fun getAll(): Flow<List<Like>>

}