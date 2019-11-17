package com.invictus.unichat.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.invictus.unichat.R
import com.invictus.unichat.chat.ChatsActivity
import com.invictus.unichat.service.AppFirebaseService
import com.invictus.unichat.util.FirestoreUtil
import com.invictus.unichat.util.StorageUtil
import kotlinx.android.synthetic.main.activity_login.*

import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

        // init firebaseauth
        auth = FirebaseAuth.getInstance()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verificationInProgress = false
                signIn(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                verificationInProgress = false
                if (e is FirebaseAuthInvalidCredentialsException) {
                    fieldPhoneNumber.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        // define behavior for the buttons
        send_code_btn.setOnClickListener {
            showProgressBar()
            startPhoneNumberVerification(fieldPhoneNumber.text.toString())
            hideProgressBar()
        }
        check_code_btn.setOnClickListener {
            val code = fieldVerificationCode.text.toString()
            if (TextUtils.isEmpty(code)) {
                fieldVerificationCode.error = "Cannot be empty."
            }
            verifyPhoneNumberWithCode(storedVerificationId, code)
        }
    }

    // the login function
    private fun signIn(credential: PhoneAuthCredential) {

        // if validation doesn't meet requirements then stop right there
        if (!validatePhoneNumber())
            return

        // if everything's ok, then continue
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirestoreUtil.initCurrentUserIfFirstTime {
                        FirestoreUtil.updateCurrentUser("", "", null, auth.currentUser?.phoneNumber ?: "", -1)
                        val successAuth = Intent(this, ChatsActivity::class.java)
                        successAuth.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(successAuth)
                        val registrationToken = FirebaseInstanceId.getInstance().token
                        AppFirebaseService.addTokenToFirestore(registrationToken)
                    }
                } else
                    Snackbar.make(this.findViewById(android.R.id.content),"Authentication failed.", Snackbar.LENGTH_SHORT).show()

            }

    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
        verificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signIn(credential)
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            token) // ForceResendingToken from callbacks
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = fieldPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.error = "Invalid phone number."
            return false
        }

        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.send_code_btn -> {
                if (!validatePhoneNumber()) {
                    return
                }

                startPhoneNumberVerification(fieldPhoneNumber.text.toString())
            }
            R.id.check_code_btn -> {
                val code = fieldVerificationCode.text.toString()
                if (TextUtils.isEmpty(code)) {
                    fieldVerificationCode.error = "Cannot be empty."
                    return
                }

                verifyPhoneNumberWithCode(storedVerificationId, code)
            }
        }
    }

    // tiny functions to control the UI while logging in

    private fun showProgressBar() {
        button_layout.visibility = View.GONE
        progressbar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressbar.visibility = View.INVISIBLE
        button_layout.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
        private const val STATE_INITIALIZED = 1
        private const val STATE_VERIFY_FAILED = 3
        private const val STATE_VERIFY_SUCCESS = 4
        private const val STATE_CODE_SENT = 2
        private const val STATE_SIGNIN_FAILED = 5
        private const val STATE_SIGNIN_SUCCESS = 6
    }
}