package com.example.zoomies_server.database.dto

import com.example.zoomies_server.model.database.entity.UserRole

open class GeneralDTO(

    var requestInsert: Boolean = false,
    var requestDelete: Boolean = false,

    var animalIdS: Int? = null,
    var nameS: String? = null,
    var speciesS: String? = null,
    var habitatS: String? = null,
    var dietS: String? = null,

    var uidS: Int? = null,
    var userNameS: String? = null,
    var passwordS: String? = null,
    var roleS: UserRole? = null,
    var emailS: String? = null,
    var phoneNumberS: String? = null,
)