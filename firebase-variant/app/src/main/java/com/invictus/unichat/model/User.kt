package com.invictus.unichat.model


data class User(val name: String, val bio: String, val profilePicturePath: String?, val phoneNumber: String, val designationLevel: Int, val registrationTokens: MutableList<String>) {
    constructor(): this("", "", null, "", -1, mutableListOf())
}