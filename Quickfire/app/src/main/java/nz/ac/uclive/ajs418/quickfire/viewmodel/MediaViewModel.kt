package nz.ac.uclive.ajs418.quickfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.Media
import nz.ac.uclive.ajs418.quickfire.repository.MediaRepository

class MediaViewModel (private val mediaRepository: MediaRepository) : ViewModel() {
    var mediaList: LiveData<List<Media>> = mediaRepository.mediaList.asLiveData()

    fun addMedia(media: Media) = viewModelScope.launch {
        mediaRepository.insert(media)
    }
}

class MediaViewModelFactory(private val mediaRepository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(mediaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}