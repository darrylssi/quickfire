package nz.ac.uclive.ajs418.quickfire

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "party")
class Party (
    @ColumnInfo var name: String,
    @ColumnInfo var members: List<User>,
    @ColumnInfo var matches: List<Movie>
        ) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}