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
}