package com.example.fileselecterapp

import java.security.SecureRandom
import kotlin.experimental.xor
import java.security.MessageDigest

class CryptoManager {

    // OTP

    internal fun oneTimePadEncrypt(plaintext: ByteArray, key: ByteArray): ByteArray? {
        if (plaintext.size != key.size) {
            // Key length must be equal to plaintext length
            return null
        }

        val ciphertext = ByteArray(plaintext.size)

        for (i in plaintext.indices) {
            // Perform bitwise XOR operation between plaintext and key
            ciphertext[i] = (plaintext[i] xor key[i]).toByte()
        }

        return ciphertext
    }

    internal fun oneTimePadDecrypt(ciphertext: ByteArray, key: ByteArray): ByteArray? {
        if (ciphertext.size != key.size) {
            // Key length must be equal to ciphertext length
            return null
        }

        val plaintext = ByteArray(ciphertext.size)

        for (i in ciphertext.indices) {
            // Perform bitwise XOR operation between ciphertext and key
            plaintext[i] = (ciphertext[i] xor key[i]).toByte()
        }

        return plaintext
    }

    internal fun generateRandomKey(size: Int): ByteArray {
        val random = SecureRandom()
        val key = ByteArray(size)
        random.nextBytes(key)
        return key
    }

    //HASH-256
    fun hashByteArrayWithSHA256(data: ByteArray): ByteArray {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        return messageDigest.digest(data)
    }

}