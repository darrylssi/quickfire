package nz.ac.uclive.ajs418.quickfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.Like
import nz.ac.uclive.ajs418.quickfire.repository.LikeRepository
import java.lang.IllegalArgumentException

class LikeViewModel(private val likeRepository: LikeRepository) : ViewModel() {
    var likes: LiveData<List<Like>> = likeRepository.likes.asLiveData()

    fun addLike(like: Like) = viewModelScope.launch {
        likeRepository.insert(like)
    }

    suspend fun getLikesByPartyAndMedia(partyId : Long, movieId : Long): Like? {
        return likeRepository.findByPartyAndMovie(partyId, movieId)
    }
}


class LikeViewModelFactory(private val likeRepository: LikeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LikeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LikeViewModel(likeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}