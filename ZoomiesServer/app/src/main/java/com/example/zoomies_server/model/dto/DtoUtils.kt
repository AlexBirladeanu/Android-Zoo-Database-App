package com.example.zoomies_server.model.dto

import com.example.zoomies_server.database.dto.AnimalDTO
import com.example.zoomies_server.model.database.entity.Animal
import com.example.zoomies_server.model.database.entity.User
import com.example.zoomies_server.model.dto.factory.AnimalDTOFactory
import com.example.zoomies_server.model.dto.factory.UserDTOFactory

object DtoUtils {

    fun toAnimalEntity(dto: AnimalDTO): Animal =
        Animal(
            dto.animalId, dto.name, dto.species, dto.habitat, dto.diet
        )

    fun fromAnimalEntity(entity: Animal): AnimalDTO =
        AnimalDTOFactory.instance.createDTO(
            animalId = entity.animalId,
            name = entity.name,
            species = entity.species,
            habitat = entity.habitat,
            diet = entity.diet
        )

    fun toUserEntity(dto: UserDTO) = User(
        dto.uid, dto.userName, dto.password, dto.role, dto.email, dto.phoneNumber
    )

    fun fromUserEntity(entity: User) = UserDTOFactory.instance.createDTO(
        uid = entity.uid,
        userName = entity.userName,
        password = entity.password,
        role = entity.role,
        email = entity.email,
        phoneNumber = entity.phoneNumber
    )
}