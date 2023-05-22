package com.example.zoomies

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.zoomies.model.AppSettingsProvider
import com.example.zoomies.model.database.AppDatabase
import com.example.zoomies.model.database.entity.User
import com.example.zoomies.model.database.entity.UserRole
import com.example.zoomies.view.components.AppNavHost
import com.example.zoomies.view.theme.ZoomiesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettingsProvider.initializeSharedPreferences(this)

        val database = AppDatabase.getDatabase(this)
        checkForAdmin(database)
        setContent {
            ZoomiesTheme {
                AppNavHost(database)
            }
        }
    }

    fun sendEmail(email: String, body: String) {
        val subject = "Zoomies Account"
        val addresses: Array<String> = listOf(email).toTypedArray()

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT,subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(Intent.createChooser(emailIntent, "Send Email"), null)
    }

    fun sendSMS(phoneNumber: String, message: String) {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        startActivity(intent)
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