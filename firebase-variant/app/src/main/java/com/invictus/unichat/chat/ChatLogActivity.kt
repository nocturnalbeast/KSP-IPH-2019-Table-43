package com.invictus.unichat.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.ByteArrayOutputStream
import java.util.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.perf.metrics.AddTrace

import com.vanniktech.emoji.EmojiPopup

import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.toIconicsSizeDp

import com.invictus.unichat.R
import com.invictus.unichat.AppConstants
import com.invictus.unichat.model.*
import com.invictus.unichat.util.*

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.activity_chat_log.*


private const val RC_SELECT_IMAGE = 2

class ChatLogActivity : AppCompatActivity() {

    private lateinit var currentChannelID: String
    private lateinit var currentUser: User
    private lateinit var otherUserID: String

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    private lateinit var emojiPopup: EmojiPopup

    private lateinit var channelKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        FirestoreUtil.getCurrentUser {
            currentUser = it
        }

        emojiPopup = EmojiPopup.Builder.fromRootView(chatbox).build(message_edittext)

        otherUserID = intent.getStringExtra(AppConstants.USER_ID)
        FirestoreUtil.getOrCreateChatChannel(otherUserID) { channelID, sharedKey ->
            currentChannelID = channelID
            channelKey = sharedKey
            messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(channelID, channelKey, this, this::updateRecyclerView)
            send_button.setOnClickListener {
                val encText = EncryptionWrapper.encryptString(message_edittext.text.toString(), channelKey)
                val messageToSend = TextMessage(
                    encText,
                    Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    otherUserID,
                    currentUser.name
                )
                message_edittext.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelID)
                message_edittext.clearFocus()
            }
            image_send_button.setOnClickListener {
                val pickImageToSendIntent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(pickImageToSendIntent, "Select image"), RC_SELECT_IMAGE)
            }
            message_edittext.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    image_send_button.visibility = View.GONE
                else
                    image_send_button.visibility = View.VISIBLE
            }
            emoji_button.setOnClickListener {
                if (emojiPopup.isShowing) {
                    emojiPopup.dismiss()
                    emoji_button.icon = IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_insert_emoticon)
                        .color(IconicsColor.colorRes(R.color.colorForeground))
                        .size(24.toIconicsSizeDp())
                } else {
                    emojiPopup.toggle()
                    emoji_button.icon = IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_keyboard)
                        .color(IconicsColor.colorRes(R.color.colorForeground))
                        .size(24.toIconicsSizeDp())
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()
            val encImageBytes = EncryptionWrapper.encryptByteArray(selectedImageBytes, channelKey)
            StorageUtil.uploadImageMessage(encImageBytes) { imagePath ->
                val messageToSend = ImageMessage(
                    imagePath,
                    Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    otherUserID,
                    currentUser.name
                )
                FirestoreUtil.sendMessage(messageToSend, currentChannelID)
            }
        }

    }

    @AddTrace(name = "ChatLogMessageLoadTrace", enabled = true)
    private fun updateRecyclerView(messages: List<Item>) {

        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatLogActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)

    }
}
