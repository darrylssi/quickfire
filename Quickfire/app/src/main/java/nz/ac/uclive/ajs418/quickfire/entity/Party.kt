package nz.ac.uclive.ajs418.quickfire.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "party")
class Party (
    @ColumnInfo var name: String,
    @ColumnInfo var members: ArrayList<Long>,
    @ColumnInfo var matches: ArrayList<Long>
        ) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}