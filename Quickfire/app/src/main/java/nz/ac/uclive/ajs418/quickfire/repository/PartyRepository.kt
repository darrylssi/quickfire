package nz.ac.uclive.ajs418.quickfire.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import nz.ac.uclive.ajs418.quickfire.dao.PartyDao
import nz.ac.uclive.ajs418.quickfire.entity.Party

class PartyRepository(private val partyDao: PartyDao) {
    val parties: Flow<List<Party>> = partyDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(party: Party) {
        partyDao.insert(party)
    }

    suspend fun getPartyById(partyId: Long): Party? {
        return partyDao.getPartyById(partyId)
    }

    suspend fun updateParty(party: Party) {
        partyDao.update(party)
    }

    suspend fun getPartyByName(partyName: String): Party? {
        return partyDao.getPartyByName(partyName)
    }

    suspend fun partyHasMatch(partyId: Long, mediaId: Long): Party? {
        return partyDao.partyHasMatch(partyId, mediaId)
    }


    suspend fun addMatch(partyId: Long, mediaId: Long) {
        // Step 1: Retrieve the party
        val party = getPartyById(partyId)

        // Step 2: Update the party's matches list
        party?.let {
            it.matches.add(mediaId)
            // Step 3: Update the party in the database
            updateParty(it)
        }
    }

    suspend fun getPartyByName(currentPartyName: String): Party? {
        return partyDao.getPartyByName(currentPartyName)
    }
}