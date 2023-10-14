package nz.ac.uclive.ajs418.quickfire.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository): ViewModel() {

//    val users: LiveData<List<User>> = userRepository.users.asLiveData()

    fun addUser(user: User) = viewModelScope.launch {
        userRepository.insert(user)
    }
}
