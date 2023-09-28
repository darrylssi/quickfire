package nz.ac.uclive.ajs418.quickfire.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class User (
    @ColumnInfo var name: String,
    @ColumnInfo var themePreference: String
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}