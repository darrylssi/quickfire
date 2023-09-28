package nz.ac.uclive.ajs418.quickfire.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import nz.ac.uclive.ajs418.quickfire.entity.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user : User): Long

    @Update
    suspend fun update (user : User): Long

}