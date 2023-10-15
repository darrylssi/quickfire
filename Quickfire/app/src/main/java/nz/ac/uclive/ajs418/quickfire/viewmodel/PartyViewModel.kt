package nz.ac.uclive.ajs418.quickfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.repository.PartyRepository
import java.lang.IllegalArgumentException

class PartyViewModel(private val partyRepository: PartyRepository) : ViewModel() {
    val parties: LiveData<List<Party>> = partyRepository.parties.asLiveData()

    fun addParty(party: Party) = viewModelScope.launch {
        partyRepository.insert(party)
    }

    fun getPartyByName(partyName: String) = viewModelScope.launch {
        partyRepository.getPartyByName(partyName)
    }

}

class PartyViewModelFactory(private val partyRepository: PartyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PartyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PartyViewModel(partyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}