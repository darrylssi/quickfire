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

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM user WHERE name = :userName")
    suspend fun getUserByName(userName: String): User?


    @Query("DELETE FROM USER")
    fun deleteAll()

}