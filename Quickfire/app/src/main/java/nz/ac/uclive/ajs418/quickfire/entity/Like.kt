package nz.ac.uclive.ajs418.quickfire.entity

import androidx.room.*

@Entity(tableName = "like_table")
class Like (
    @ColumnInfo var partyId: Long,
    @ColumnInfo var movieId: Long
//    @ColumnInfo var likedBy: List<User>
        ){
    @PrimaryKey(autoGenerate = true) var id: Long = 0

}