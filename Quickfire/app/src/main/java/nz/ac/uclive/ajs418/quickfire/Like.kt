package nz.ac.uclive.ajs418.quickfire

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "like")
class Like (
    @ColumnInfo var partyId: Long,
    @ColumnInfo var movieId: Long,
    @ColumnInfo var likedBy: List<User>
        ){
    @PrimaryKey(autoGenerate = true) var id: Long = 0

}