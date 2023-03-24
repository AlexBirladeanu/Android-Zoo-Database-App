package com.example.zoomies.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.database.AppDatabase
import com.example.zoomies.database.entity.User
import com.example.zoomies.database.entity.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersViewModel(db: AppDatabase): ViewModel() {

    private val database: AppDatabase

    private val userListLock = Any()
    private val _userList: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val userList = _userList.asStateFlow()

    init {
        database = db
        resetList()
    }

    private suspend fun getAll(): List<User>? =
        withContext(Dispatchers.IO) {
            return@withContext database.userDao().getAll()
        }

    fun insert(id: Int? = null, username: String, password: String, role: UserRole = UserRole.EMPLOYEE, email: String, phoneNumber: String) {
        val user = User(
            uid = id,
            userName = username,
            password = password,
            role = role,
            email = email,
            phoneNumber = phoneNumber
        )
        Log.w("Reparatii", user.toString())
        addToComposableView(user)
        viewModelScope.launch(Dispatchers.IO) {
            database.userDao().insert(user)
        }
    }

    fun delete(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            database.userDao().delete(user)
            resetList()
        }
    }

    private fun addToComposableView(user: User) {
        synchronized(userListLock) {
            _userList.value += user
            _userList.value = _userList.value.sortedBy { it.uid }
        }
    }

    private fun resetList() {
        viewModelScope.launch(Dispatchers.IO) {
            _userList.value = listOf()
            getAll()?.forEach {
                addToComposableView(it)
            }
        }
    }

    companion object {
        var selectedUser: User? = null
    }
}