package com.invictus.unichat.service

import java.lang.NullPointerException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService

import com.invictus.unichat.util.FirestoreUtil

class AppFirebaseService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val newRegistrationToken = p0
        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(newRegistrationToken)
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FirestoreUtil.getFCMRegistrationTokens { tokens ->

                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens
                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)

            }
        }
    }

}