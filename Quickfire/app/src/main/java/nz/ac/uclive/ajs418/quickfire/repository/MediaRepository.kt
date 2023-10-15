package nz.ac.uclive.ajs418.quickfire.repository

import androidx.annotation.WorkerThread
import nz.ac.uclive.ajs418.quickfire.dao.MediaDao
import nz.ac.uclive.ajs418.quickfire.entity.Media
import kotlinx.coroutines.flow.Flow

class MediaRepository (private val mediaDao: MediaDao){
    val mediaList: Flow<List<Media>> = mediaDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(media: Media) {
        mediaDao.insert(media)
    }

    fun getRandomMedia(): Media? {
        return mediaDao.getRandomMedia()
    }

    fun getMediaById(mediaId: Long): Media? {
        return mediaDao.getMediaById(mediaId)
    }

}