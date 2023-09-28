package nz.ac.uclive.ajs418.quickfire.database

import nz.ac.uclive.ajs418.quickfire.dao.LikeDao
import nz.ac.uclive.ajs418.quickfire.entity.Like
import android.content.Context
import androidx.room.*
import nz.ac.uclive.ajs418.quickfire.entity.User

@Database(entities =  [User::class, Like::class], version = 1)
abstract class QuickfireDatabase : RoomDatabase() {
    abstract fun likeDao() : LikeDao

    companion object {
        @Volatile
        private var INSTANCE: QuickfireDatabase? = null

        fun getDatabase(context: Context): QuickfireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickfireDatabase::class.java,
                    "like_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}