package nz.ac.uclive.ajs418.quickfire.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "party")
class Party : Parcelable {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @ColumnInfo var name: String
    @ColumnInfo var members: ArrayList<Long>
    @ColumnInfo var matches: ArrayList<Long>

    constructor(name: String, members: ArrayList<Long>, matches: ArrayList<Long>, media_type : Boolean) {
        this.name = name
        this.members = members
        this.matches = matches
    }

    constructor(parcel: Parcel) {
        id = parcel.readLong()
        name = parcel.readString() ?: ""
        val membersSize = parcel.readInt()
        members = ArrayList<Long>().apply {
            for (i in 0 until membersSize) {
                add(parcel.readLong())
            }
        }
        val matchesSize = parcel.readInt()
        matches = ArrayList<Long>().apply {
            for (i in 0 until matchesSize) {
                add(parcel.readLong())
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeInt(members.size)
        for (member in members) {
            parcel.writeLong(member)
        }
        parcel.writeInt(matches.size)
        for (match in matches) {
            parcel.writeLong(match)
        }
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Party> {
        override fun createFromParcel(parcel: Parcel): Party {
            return Party(parcel)
        }

        override fun newArray(size: Int): Array<Party?> {
            return arrayOfNulls(size)
        }
    }
}
