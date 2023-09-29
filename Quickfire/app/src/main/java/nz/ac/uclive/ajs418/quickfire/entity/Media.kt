package nz.ac.uclive.ajs418.quickfire.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
class Media (
    @ColumnInfo var title: String,
    @ColumnInfo var year: Long,
    @ColumnInfo var type: String,
    @ColumnInfo var rating: Float?,
    @ColumnInfo var synopsis: String,
    @ColumnInfo var imgUrl: String?
        ){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}