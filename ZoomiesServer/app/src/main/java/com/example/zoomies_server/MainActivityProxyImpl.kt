package com.example.zoomies_server

import com.example.zoomies_server.database.dto.AnimalDTO
import com.example.zoomies_server.model.dto.DtoUtils
import com.example.zoomies_server.model.dto.UserDTO
import com.example.zoomies_server.model.proxy.MainActivityProxy

class MainActivityProxyImpl(private val activity: MainActivity) : MainActivityProxy {

    override fun startTransferToClient() {
        val animals = MainActivity.database.animalDao().getAll()?.map { DtoUtils.fromAnimalEntity(it) }
        val users = MainActivity.database.userDao().getAll()?.map { DtoUtils.fromUserEntity(it) }
        Thread(activity.Sender(animals, users)).start()
    }
}