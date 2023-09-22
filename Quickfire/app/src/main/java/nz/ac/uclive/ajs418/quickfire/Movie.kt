package nz.ac.uclive.ajs418.quickfire

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "movie")
class Movie (
    @ColumnInfo var title: String,
    @ColumnInfo var year: Long,
    @ColumnInfo var genre: String,
    @ColumnInfo var rating: Float,

        ){
}