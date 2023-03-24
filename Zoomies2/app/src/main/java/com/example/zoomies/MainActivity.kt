package com.example.zoomies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.zoomies.database.AppDatabase
import com.example.zoomies.database.entity.User
import com.example.zoomies.database.entity.UserRole
import com.example.zoomies.ui.navigation.AppNavHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        checkForAdmin(database)
        setContent {
            AppNavHost(database)
        }
    }

    private fun checkForAdmin(database: AppDatabase) {
        lifecycleScope.launch(Dispatchers.IO) {
            var adminFound = false
            database.userDao().getAll()?.forEach {
                if (it.role == UserRole.ADMIN) {
                    adminFound = true
                }
            }
            if (!adminFound) {
                database.userDao().insert(
                    User(
                        uid = null,
                        userName = "admin",
                        password = "admin",
                        role = UserRole.ADMIN,
                        email = "123@123.com",
                        phoneNumber = "0737546695"
                    )
                )
            }
        }
    }
}