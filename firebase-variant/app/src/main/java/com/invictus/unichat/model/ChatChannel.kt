package com.invictus.unichat.model


data class ChatChannel(val userIds: MutableList<String>, val sharedKey: String) {
    constructor() : this(mutableListOf(), "")
}