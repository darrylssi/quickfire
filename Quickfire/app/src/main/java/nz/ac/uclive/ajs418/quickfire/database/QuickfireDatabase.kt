package nz.ac.uclive.ajs418.quickfire.database

import nz.ac.uclive.ajs418.quickfire.dao.LikeDao
import nz.ac.uclive.ajs418.quickfire.entity.Like
import android.content.Context
import androidx.room.*
import nz.ac.uclive.ajs418.quickfire.dao.MediaDao
import nz.ac.uclive.ajs418.quickfire.dao.PartyDao
import nz.ac.uclive.ajs418.quickfire.dao.UserDao
import nz.ac.uclive.ajs418.quickfire.entity.Converters
import nz.ac.uclive.ajs418.quickfire.entity.Media
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.entity.User

@Database(entities =  [User::class, Like::class, Party::class, Media::class], version = 1)
@TypeConverters(Converters::class)
abstract class QuickfireDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun likeDao() : LikeDao
    abstract fun partyDao() : PartyDao
    abstract fun mediaDao() : MediaDao

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