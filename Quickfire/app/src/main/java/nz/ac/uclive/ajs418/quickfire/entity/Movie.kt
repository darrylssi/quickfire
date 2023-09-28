package nz.ac.uclive.ajs418.quickfire.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
class Movie (
    @ColumnInfo var title: String,
    @ColumnInfo var year: Long,
    @ColumnInfo var genre: String,
    @ColumnInfo var rating: Float
        ){
    @PrimaryKey(autoGenerate = true) var id: Long = 0 // Auto-generated ID
}