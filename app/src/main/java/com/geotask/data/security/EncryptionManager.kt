package com.geotask.data.security

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Менеджер для шифрования/расшифровки данных с использованием AES-GCM
 * Ключ хранится в защищённом хранилище Android
 */
class EncryptionManager {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val TAG_LENGTH_BIT = 128
        private const val IV_LENGTH_BYTE = 12
    }

    private val keyStore = SecurityKeyStore()

    /**
     * Шифрует строку и возвращает Base64-закодированный результат с IV
     */
    fun encrypt(plaintext: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val key = keyStore.getOrCreateKey()
            
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            
            // Конкатенируем IV + зашифрованные данные
            val encryptedData = iv + ciphertext
            return Base64.encodeToString(encryptedData, Base64.DEFAULT)
        } catch (e: Exception) {
            throw EncryptionException("Encryption failed", e)
        }
    }

    /**
     * Расшифровывает Base64-закодированную строку
     */
    fun decrypt(encryptedText: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val key = keyStore.getOrCreateKey()
            
            val encryptedData = Base64.decode(encryptedText, Base64.DEFAULT)
            
            // Извлекаем IV (первые 12 байт)
            val iv = encryptedData.copyOfRange(0, IV_LENGTH_BYTE)
            val ciphertext = encryptedData.copyOfRange(IV_LENGTH_BYTE, encryptedData.size)
            
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            
            val plaintext = cipher.doFinal(ciphertext)
            return String(plaintext, Charsets.UTF_8)
        } catch (e: Exception) {
            throw EncryptionException("Decryption failed", e)
        }
    }

    /**
     * Шифрует Long и возвращает String
     */
    fun encryptLong(value: Long): String = encrypt(value.toString())

    /**
     * Расшифровывает String в Long
     */
    fun decryptLong(encrypted: String): Long = decrypt(encrypted).toLong()

    /**
     * Шифрует Double и возвращает String
     */
    fun encryptDouble(value: Double): String = encrypt(value.toString())

    /**
     * Расшифровывает String в Double
     */
    fun decryptDouble(encrypted: String): Double = decrypt(encrypted).toDouble()

    /**
     * Шифрует Int и возвращает String
     */
    fun encryptInt(value: Int): String = encrypt(value.toString())

    /**
     * Расшифровывает String в Int
     */
    fun decryptInt(encrypted: String): Int = decrypt(encrypted).toInt()

    /**
     * Шифрует Boolean и возвращает String
     */
    fun encryptBoolean(value: Boolean): String = encrypt(value.toString())

    /**
     * Расшифровывает String в Boolean
     */
    fun decryptBoolean(encrypted: String): Boolean = decrypt(encrypted).toBoolean()
}

class EncryptionException(message: String, cause: Throwable) : Exception(message, cause)
