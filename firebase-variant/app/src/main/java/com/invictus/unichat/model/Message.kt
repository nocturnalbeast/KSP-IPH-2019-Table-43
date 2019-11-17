package com.invictus.unichat.model

import java.util.*


object messageType {

    const val TEXT = "text"
    const val IMAGE = "image"

}

interface Message {

    val time: Date
    val senderID: String
    val recipientID: String
    val senderName: String
    val type: String

}