package com.example.zoomies_server.viewmodel

import androidx.lifecycle.ViewModel
import com.example.zoomies_server.MainActivity
import com.example.zoomies_server.MainActivityProxyImpl
import com.example.zoomies_server.database.dto.AnimalDTO
import com.example.zoomies_server.model.dto.DtoUtils
import com.example.zoomies_server.model.dto.UserDTO
import com.example.zoomies_server.model.proxy.MainActivityProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private lateinit var activityProxy: MainActivityProxy

    fun initContext(mainActivity: MainActivity) {
        activityProxy = MainActivityProxyImpl(mainActivity)
    }

    fun insertAnimal(animalDTO: AnimalDTO) {
        val entity = DtoUtils.toAnimalEntity(animalDTO)
        MainActivity.database.animalDao().insert(entity)
        CoroutineScope(Dispatchers.IO + Job()).launch {
            activityProxy.startTransferToClient()
        }
    }

    fun deleteAnimal(animalDTO: AnimalDTO) {
        val entity = DtoUtils.toAnimalEntity(animalDTO)
        MainActivity.database.animalDao().delete(entity)
        CoroutineScope(Dispatchers.IO + Job()).launch {
            activityProxy.startTransferToClient()
        }
    }

    fun insertUser(userDTO: UserDTO) {
        val entity = DtoUtils.toUserEntity(userDTO)
        MainActivity.database.userDao().insert(entity)
        CoroutineScope(Dispatchers.IO + Job()).launch {
            activityProxy.startTransferToClient()
        }
    }

    fun deleteUser(userDTO: UserDTO) {
        val entity = DtoUtils.toUserEntity(userDTO)
        MainActivity.database.userDao().delete(entity)
        CoroutineScope(Dispatchers.IO + Job()).launch {
            activityProxy.startTransferToClient()
        }
    }
}