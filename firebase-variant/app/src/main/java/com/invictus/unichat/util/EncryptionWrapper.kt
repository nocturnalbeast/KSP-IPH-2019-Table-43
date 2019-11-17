package com.invictus.unichat.util

import org.jasypt.encryption.pbe.StandardPBEByteEncryptor
import org.jasypt.util.text.AES256TextEncryptor

object EncryptionWrapper {

    fun encryptString(text: String, password: String): String {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(password)
        return encryptor.encrypt(text)
    }

    fun decryptString(text: String, password: String): String {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(password)
        return encryptor.decrypt(text)
    }

    fun encryptByteArray(bytes: ByteArray, password: String): ByteArray {
        val encryptor = StandardPBEByteEncryptor()
        encryptor.setPassword(password)
        return encryptor.encrypt(bytes)
    }

    fun decryptByteArray(bytes: ByteArray, password: String): ByteArray {
        val encryptor = StandardPBEByteEncryptor()
        encryptor.setPassword(password)
        return encryptor.decrypt(bytes)
    }

}
