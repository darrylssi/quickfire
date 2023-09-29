package nz.ac.uclive.ajs418.quickfire.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import nz.ac.uclive.ajs418.quickfire.entity.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user : User): Long

    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

}