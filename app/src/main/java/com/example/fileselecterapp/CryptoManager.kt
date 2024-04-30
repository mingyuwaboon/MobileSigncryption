package com.example.fileselecterapp

import java.security.SecureRandom
import kotlin.experimental.xor
import java.security.MessageDigest
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.PrivateKey
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher
import java.security.Security
import android.util.Log
import java.security.KeyPair
import java.security.Signature
import java.security.interfaces.RSAPublicKey



class CryptoManager {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    //ECC
    fun generateECCKeyPair(context: Context, alias: String): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setUserAuthenticationRequired(false)
            .build()
        keyPairGenerator.initialize(keyGenParameterSpec)
        return keyPairGenerator.generateKeyPair()
    }

    // Sign the data with the private key
    fun signData(privateKey: PrivateKey, data: ByteArray): ByteArray {
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    //RSA
    fun generateAndStoreRSAKeyPair(context: Context, alias: String) {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            "AndroidKeyStore"
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(2048) // Key size in bits
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP) // Encryption padding
            .build()
        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.generateKeyPair()
    }

    fun encryptRSA(alias: String, plaintext: ByteArray): ByteArray? {
        val publicKey = keyStore.getCertificate(alias)?.publicKey ?: return null

        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        return cipher.doFinal(plaintext)
    }

    fun decryptRSA(alias: String, encryptedData: ByteArray): ByteArray? {
        val privateKey = keyStore.getKey(alias, null) as? PrivateKey ?: return null

        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        return cipher.doFinal(encryptedData)
    }

    fun encryptLargeDataWithRSA(alias: String, data: ByteArray): ByteArray? {
        val chunkSize = 190 // Adjust chunk size based on RSA key size and padding scheme
        val encryptedChunks = mutableListOf<ByteArray>()

        // Split data into chunks and encrypt each chunk with RSA
        var startIndex = 0
        while (startIndex < data.size) {
            val endIndex = minOf(startIndex + chunkSize, data.size)
            val chunk = data.sliceArray(startIndex until endIndex)
            val encryptedChunk = encryptRSA(alias, chunk)
            if (encryptedChunk != null) {
                encryptedChunks.add(encryptedChunk)
            } else {
                // Handle encryption failure
                return null
            }
            startIndex += chunkSize
        }

        // Concatenate encrypted chunks into a single ByteArray
        return concatenateByteArrays(encryptedChunks)
    }

    // Concatenate a list of byte arrays into a single byte array
    fun concatenateByteArrays(byteArrays: List<ByteArray>): ByteArray {
        val totalLength = byteArrays.sumOf { it.size }
        val result = ByteArray(totalLength)
        var currentIndex = 0
        for (array in byteArrays) {
            array.copyInto(result, destinationOffset = currentIndex)
            currentIndex += array.size
        }
        return result
    }


    //OTP
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