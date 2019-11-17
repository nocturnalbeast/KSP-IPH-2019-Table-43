package com.invictus.unichat.model

import java.util.*


data class TextMessage(
    val text: String,
    override val time: Date,
    override val senderID: String,
    override val recipientID: String,
    override val senderName: String,
    override val type: String = messageType.TEXT)
    : Message {

    constructor() : this("", Date(0), "", "", "")

}