package nz.ac.uclive.ajs418.quickfire.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nz.ac.uclive.ajs418.quickfire.entity.Party

@Dao
interface PartyDao {

    @Insert
    suspend fun insert(party : Party): Long

    @Query("SELECT * FROM party")
    fun getAll(): Flow<List<Party>>

    @Query("SELECT * FROM party WHERE id = :partyId")
    suspend fun getPartyById(partyId: Long):Party?

    @Update
    suspend fun update(party: Party)
}