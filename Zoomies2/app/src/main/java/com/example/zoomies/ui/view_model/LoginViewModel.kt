package com.example.zoomies.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.database.AppDatabase
import com.example.zoomies.database.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(db: AppDatabase): ViewModel() {

    private val database: AppDatabase

    val loggedInStatus: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)

    init {
        database = db
    }

    private suspend fun getAll(): List<User>? =
        withContext(Dispatchers.IO) {
            return@withContext database.userDao().getAll()
        }

    fun loginUser(userName: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var userFound = false
            getAll()?.forEach{
                if (it.userName == userName && it.password == password) {
                    activeUser = it
                    userFound = true
                }
            }
            loggedInStatus.emit(userFound)
        }
    }

    companion object {
        var activeUser: User? = null
    }
}