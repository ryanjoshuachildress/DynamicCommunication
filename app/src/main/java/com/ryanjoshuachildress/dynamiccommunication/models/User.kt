package com.ryanjoshuachildress.dynamiccommunication.models

class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Boolean = false,
    val isModerator: Boolean = false,
    val isAdmin: Boolean = false
        )