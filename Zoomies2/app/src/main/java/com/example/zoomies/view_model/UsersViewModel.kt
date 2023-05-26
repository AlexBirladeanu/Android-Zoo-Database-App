package com.example.zoomies.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.MainActivity
import com.example.zoomies.model.AppSettingsProvider
import com.example.zoomies.model.dto.UserDTO
import com.example.zoomies.model.dto.UserRole
import com.example.zoomies.model.dto.factory.UserDTOFactory
import com.example.zoomies.model.observer.LanguageEventHandler
import com.example.zoomies.model.observer.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersViewModel(
    val languageEventHandler: LanguageEventHandler,
    private val refreshPage: () -> Unit = {}
) : ViewModel(),
    Observer {


    private val userListLock = Any()
    private val _userList: MutableStateFlow<List<UserDTO>> = MutableStateFlow(listOf())
    val userList = _userList.asStateFlow()

    private val _isEmailNotification: MutableStateFlow<Boolean> = MutableStateFlow(
        AppSettingsProvider.isEmailNotification()
    )
    val isEmailNotification = _isEmailNotification.asStateFlow()

    init {
        languageEventHandler.attach(this)

        MainActivity.serverRequestFinished.observeForever {
            resetList()
        }
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
        val user = UserDTOFactory.instance.createDTO(
            uid = id,
            userName = username,
            password = password,
            role = role,
            email = email,
            phoneNumber = phoneNumber
        )
        (context as MainActivity).requestInsertUser(user)

        val message = "Your new account credentials are:\n" +
                "username: $username \n password: $password \n phoneNumber: $phoneNumber"
        if (_isEmailNotification.value) {
            context.sendEmail(email, message)
        } else {
            context.sendSMS(phoneNumber, message)
        }
    }

    fun delete(user: UserDTO, context: Context) {
        (context as MainActivity).requestDeleteUser(user)

        val message = "Your account has been removed!"
        if (_isEmailNotification.value) {
            (context as MainActivity).sendEmail(user.email, message)
        } else {
            (context as MainActivity).sendSMS(user.phoneNumber, message)
        }
    }

    private fun resetList() {
        MainActivity.users?.let {
            _userList.value = it
        }
    }

    fun setIsEmailNotification(isEnabled: Boolean) {
        _isEmailNotification.value = isEnabled
        AppSettingsProvider.setIsEmailNotification(isEnabled)
    }

    companion object {
        var selectedUser: UserDTO? = null
    }

    override fun onLanguageChanged() {
        refreshPage()
    }
}