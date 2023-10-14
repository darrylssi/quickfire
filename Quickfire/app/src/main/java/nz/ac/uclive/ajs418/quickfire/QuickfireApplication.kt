package nz.ac.uclive.ajs418.quickfire

import android.app.Application
import androidx.room.Room
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository

class QuickfireApplication : Application() {
    val userRepository by lazy {UserRepository(database.userDao()) }

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