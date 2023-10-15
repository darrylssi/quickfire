package nz.ac.uclive.ajs418.quickfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository
import androidx.lifecycle.*
import java.lang.IllegalArgumentException


class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val users: LiveData<List<User>> = userRepository.users.asLiveData()

    fun addUser(user: User) = viewModelScope.launch {
        userRepository.insert(user)
    }

    suspend fun deleteUsers() = viewModelScope.launch {
        userRepository.deleteAll()
    }

}


class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}