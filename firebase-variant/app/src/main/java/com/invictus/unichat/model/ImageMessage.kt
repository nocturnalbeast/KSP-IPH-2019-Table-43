package com.invictus.unichat.model

import java.util.*


data class ImageMessage(
    val imagePath: String,
    override val time: Date,
    override val senderID: String,
    override val recipientID: String,
    override val senderName: String,
    override val type: String = messageType.IMAGE)
    : Message {

    constructor() : this("", Date(0), "", "", "")

}