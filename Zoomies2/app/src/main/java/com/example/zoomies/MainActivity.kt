package com.example.zoomies

import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.zoomies.model.AppSettingsProvider
import com.example.zoomies.model.dto.AnimalDTO
import com.example.zoomies.model.dto.SerializationUtils
import com.example.zoomies.model.dto.GeneralDTO
import com.example.zoomies.model.dto.UserDTO
import com.example.zoomies.model.dto.factory.AnimalDTOFactory
import com.example.zoomies.model.dto.factory.UserDTOFactory
import com.example.zoomies.view.components.AppNavHost
import com.example.zoomies.view.theme.ZoomiesTheme
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettingsProvider.initializeSharedPreferences(this)
        getLocalIpAddress()?.let {
            SERVER_IP = it
        }
        setContent {
            ZoomiesTheme {
                AppNavHost()
            }
        }

    }

    fun sendEmail(email: String, body: String) {
        val subject = "Zoomies Account"
        val addresses: Array<String> = listOf(email).toTypedArray()

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
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

    @Throws(UnknownHostException::class)
    private fun getLocalIpAddress(): String? {
        val wifiManager = (applicationContext.getSystemService(WIFI_SERVICE) as WifiManager)
        val wifiInfo = wifiManager.connectionInfo
        val ipInt = wifiInfo.ipAddress
        return InetAddress.getByAddress(
            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()
        ).hostAddress
    }

    internal class Connector : Runnable {
        override fun run() {
            val socket: Socket
            try {
                socket = Socket(SERVER_IP, SERVER_PORT)
                output = PrintWriter(socket.getOutputStream())
                input = BufferedReader(InputStreamReader(socket.getInputStream()))

                Thread(Receiver()).start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    internal class Receiver : Runnable {
        override fun run() {
            while (true) {
                try {
                    val message = input!!.readLine()
                    if (message != null) {
                        val transfer: List<GeneralDTO> = SerializationUtils.castTransfer(message)
                        if (transfer.isNotEmpty() && transfer.first().animalIdS != null) {
                            val animalList: MutableList<AnimalDTO> = mutableListOf()
                            transfer.forEach {
                                animalList.add(
                                    AnimalDTOFactory.instance.createDTO(
                                        animalId = it.animalIdS,
                                        name = it.nameS!!,
                                        species = it.speciesS!!,
                                        habitat = it.habitatS!!,
                                        diet = it.dietS!!
                                    )
                                )
                            }
                            animals = animalList
                            serverRequestFinished.postValue(Unit)
                        } else {
                            if (transfer.isNotEmpty() && transfer.first().userNameS != null) {
                                val userList: MutableList<UserDTO> = mutableListOf()
                                transfer.forEach {
                                    userList.add(
                                        UserDTOFactory.instance.createDTO(
                                            uid = it.uidS,
                                            userName = it.userNameS!!,
                                            password = it.passwordS!!,
                                            role = it.roleS!!,
                                            email = it.emailS!!,
                                            phoneNumber = it.phoneNumberS!!
                                        )
                                    )
                                }
                                users = userList
                                serverRequestFinished.postValue(Unit)
                            }
                        }
                    } else {
                        connectorThread = Thread(Connector())
                        connectorThread.start()
                        return
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal class Sender(private val generalDTO: GeneralDTO) : Runnable {
        override fun run() {
            output!!.println(Gson().toJson(generalDTO))
            output!!.flush()
        }
    }


    fun attemptConnectionToServer(ip: String, port: Int = 8080) {
        SERVER_IP = ip
        SERVER_PORT = port
        connectorThread = Thread(Connector());
        connectorThread.start();
    }

    fun requestInsertAnimal(animalDTO: AnimalDTO) {
        val generalDTO: GeneralDTO = animalDTO
        generalDTO.requestInsert = true
        Thread(Sender(generalDTO)).start()
    }

    fun requestInsertUser(userDTO: UserDTO) {
        val generalDTO: GeneralDTO = userDTO
        generalDTO.requestInsert = true
        Thread(Sender(generalDTO)).start()
    }

    fun requestDeleteAnimal(animalDTO: AnimalDTO) {
        val generalDTO: GeneralDTO = animalDTO
        generalDTO.requestDelete = true
        Thread(Sender(generalDTO)).start()
    }

    fun requestDeleteUser(userDTO: UserDTO) {
        val generalDTO: GeneralDTO = userDTO
        generalDTO.requestDelete = true
        Thread(Sender(generalDTO)).start()
    }

    companion object {
        var SERVER_IP: String = ""
        var SERVER_PORT = 8080

        var output: PrintWriter? = null
        var input: BufferedReader? = null

        var animals: List<AnimalDTO>? = null
        var users: List<UserDTO>? = null

        val serverRequestFinished = MutableLiveData<Unit>()

        private lateinit var connectorThread: Thread
    }
}