package nz.ac.uclive.ajs418.quickfire.repository

import nz.ac.uclive.ajs418.quickfire.entity.Like
import androidx.annotation.WorkerThread
import nz.ac.uclive.ajs418.quickfire.dao.LikeDao
import kotlinx.coroutines.flow.Flow

class LikeRepository(private val likeDao: LikeDao) {
    val likes: Flow<List<Like>> = likeDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(like: Like) {
        likeDao.insert(like)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun findByPartyAndMovie(partyId: Long, movieId: Long) : Like? {
        return likeDao.findByPartyAndMovie(partyId, movieId)
    }

}