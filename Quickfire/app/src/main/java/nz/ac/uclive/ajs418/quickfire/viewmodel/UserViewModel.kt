package nz.ac.uclive.ajs418.quickfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import java.lang.IllegalArgumentException


class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val users: LiveData<List<User>> = userRepository.users.asLiveData()
    var currentId: Long = 0L

    fun addUser(user: User) = viewModelScope.launch {
        userRepository.insert(user)
    }

    fun setId(userId : Long) {
        currentId = userId
    }


    suspend fun deleteUsers() = viewModelScope.launch {
        userRepository.deleteAll()
    }

    suspend fun getUserById(userId: Long): User? {
        return userRepository.getUserById(userId)
    }


    suspend fun getUserByName(userName: String): User? {
        return userRepository.getUserByName(userName)
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