package com.invictus.unichat.util

import android.content.Context
import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import com.google.firebase.perf.metrics.AddTrace

import com.invictus.unichat.model.*
import com.invictus.unichat.recyclerview.item.*

import com.xwray.groupie.kotlinandroidextensions.Item


object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    @AddTrace(name = "CreateUserTrace", enabled = true)
    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                var auth = FirebaseAuth.getInstance()
                val newUser = User(auth.currentUser?.displayName ?: "", "", null, auth.currentUser?.phoneNumber ?: "", -1, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    @AddTrace(name = "UpdateUserTrace", enabled = true)
    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null, phoneNumber: String = "", designationLevel: Int = -1) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePicturePath != null) userFieldMap["profilePicturePath"] = profilePicturePath
        if (phoneNumber.isNotBlank()) userFieldMap["phoneNumber"] = phoneNumber
        if (designationLevel != -1) userFieldMap["designationLevel"] = designationLevel
        currentUserDocRef.update(userFieldMap)
    }

    @AddTrace(name = "GetCurrentUserTrace", enabled = true)
    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef
            .get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null) {
                    Log.e("firestore", "User listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach{
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    @AddTrace(name = "GetOrCreateChatChannelTrace", enabled = true)
    fun getOrCreateChatChannel(otherUserID: String, onComplete: (channelID: String, sharedKey: String) -> Unit) {

        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserID).get().addOnSuccessListener {
                if (it.exists()) {
                    chatChannelsCollectionRef.document(it["channelID"] as String).get().addOnCompleteListener { res ->
                        onComplete(it["channelID"] as String, res.result!!["sharedKey"] as String)
                    }
                    return@addOnSuccessListener
                }

                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
                val sharedKey = getRandomString(32)

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserID, otherUserID), sharedKey))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserID)
                    .set(mapOf("channelID" to newChannel.id))

                firestoreInstance
                    .collection("users")
                    .document(otherUserID)
                    .collection("engagedChatChannels")
                    .document(currentUserID).set(mapOf("channelID" to newChannel.id))

                onComplete(newChannel.id, sharedKey)
            }

    }

    @AddTrace(name = "MessagesListenerTrace", enabled = true)
    fun addChatMessagesListener(channelID: String, channelKey: String, context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {

        return chatChannelsCollectionRef
            .document(channelID)
            .collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("firestore", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == messageType.TEXT) {
                        val encMessage = it.toObject(TextMessage::class.java)!!
                        val decMessage = TextMessage(
                            EncryptionWrapper.decryptString(encMessage.text, channelKey),
                            encMessage.time,
                            encMessage.senderID,
                            encMessage.recipientID,
                            encMessage.senderName
                        )
                        items.add(TextMessageItem(decMessage, context))
                    } else {
                        val encMessage = it.toObject(ImageMessage::class.java)!!
                        val decMessage = ImageMessage(
                            encMessage.imagePath,
                            encMessage.time,
                            encMessage.senderID,
                            encMessage.recipientID,
                            encMessage.senderName
                        )
                        items.add(ImageMessageItem(decMessage, channelKey, context))
                    }
                    return@forEach
                }
                onListen(items)
            }

    }

    @AddTrace(name = "SendMessageTrace", enabled = true)
    fun sendMessage(message: Message, channelID: String) {
        chatChannelsCollectionRef
            .document(channelID)
            .collection("messages")
            .add(message)
    }

    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            val user =  documentSnapshot.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}