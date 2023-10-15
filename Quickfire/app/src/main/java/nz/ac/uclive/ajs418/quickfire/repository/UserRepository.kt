package nz.ac.uclive.ajs418.quickfire.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import nz.ac.uclive.ajs418.quickfire.dao.UserDao
import nz.ac.uclive.ajs418.quickfire.entity.User

class UserRepository(private val userDao: UserDao) {
    val users: Flow<List<User>> = userDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User){
        userDao.insert(user)
    }

    suspend fun deleteAll() {
        userDao.deleteAll()
    }
}