package com.example.zoomies.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.MainActivity
import com.example.zoomies.model.dto.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(): ViewModel() {


    val loggedInStatus: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)

    init {
    }

    fun loginUser(userName: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var userFound = false
            MainActivity.users?.forEach{
                if (it.userName == userName && it.password == password) {
                    activeUser = it
                    userFound = true
                }
            }
            loggedInStatus.emit(userFound)
        }
    }

    companion object {
        var activeUser: UserDTO? = null
    }
}