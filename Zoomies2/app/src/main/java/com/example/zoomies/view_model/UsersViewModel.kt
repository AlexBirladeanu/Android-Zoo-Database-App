package com.example.zoomies.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.MainActivity
import com.example.zoomies.model.AppSettingsProvider
import com.example.zoomies.model.database.AppDatabase
import com.example.zoomies.model.database.entity.User
import com.example.zoomies.model.database.entity.UserRole
import com.example.zoomies.model.observer.LanguageEventHandler
import com.example.zoomies.model.observer.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersViewModel(
    val languageEventHandler: LanguageEventHandler,
    db: AppDatabase,
    private val refreshPage: () -> Unit = {}
) : ViewModel(),
    Observer {

    private val database: AppDatabase

    private val userListLock = Any()
    private val _userList: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val userList = _userList.asStateFlow()

    private val _isEmailNotification: MutableStateFlow<Boolean> = MutableStateFlow(
        AppSettingsProvider.isEmailNotification()
    )
    val isEmailNotification = _isEmailNotification.asStateFlow()

    init {
        languageEventHandler.attach(this)

        database = db
        resetList()
    }

    private suspend fun getAll(): List<User>? =
        withContext(Dispatchers.IO) {
            return@withContext database.userDao().getAll()
        }

    fun insert(
        id: Int? = null,
        username: String,
        password: String,
        role: UserRole = UserRole.EMPLOYEE,
        email: String,
        phoneNumber: String,
        context: Context
    ) {
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
        val message = "Your new account credentials are:\n" +
                "username: $username \n password: $password \n phoneNumber: $phoneNumber"
        if (_isEmailNotification.value) {
            (context as MainActivity).sendEmail(email, message)
        } else {
            (context as MainActivity).sendSMS(phoneNumber, message)
        }
    }

    fun delete(user: User, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            database.userDao().delete(user)
            resetList()
        }
        val message = "Your account has been removed!"
        if (_isEmailNotification.value) {
            (context as MainActivity).sendEmail(user.email, message)
        } else {
            (context as MainActivity).sendSMS(user.phoneNumber, message)
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

    fun setIsEmailNotification(isEnabled: Boolean) {
        _isEmailNotification.value = isEnabled
        AppSettingsProvider.setIsEmailNotification(isEnabled)
    }

    companion object {
        var selectedUser: User? = null
    }

    override fun onLanguageChanged() {
        refreshPage()
    }
}