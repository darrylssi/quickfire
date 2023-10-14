package nz.ac.uclive.ajs418.quickfire

import android.app.Application
import androidx.room.Room
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.repository.LikeRepository
import nz.ac.uclive.ajs418.quickfire.repository.MediaRepository
import nz.ac.uclive.ajs418.quickfire.repository.PartyRepository
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository

class QuickfireApplication : Application() {
    val userRepository by lazy { UserRepository(database.userDao()) }
    val partyRepository by lazy { PartyRepository(database.partyDao()) }
    val likeRepository by lazy { LikeRepository(database.likeDao()) }
    val mediaRepository by lazy { MediaRepository(database.mediaDao()) }

    companion object {
        lateinit var database: QuickfireDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(applicationContext, QuickfireDatabase::class.java, "quickfire-database")
            .build()
    }
}