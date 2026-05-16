package com.geotask.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Управляет криптографическими ключами, хранящимися в защищённом хранилище Android
 */
internal class SecurityKeyStore {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "geotask_encryption_key"
        private const val KEY_SIZE = 256
    }

    private val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    /**
     * Получает существующий ключ или создаёт новый, если его нет
     */
    fun getOrCreateKey(): SecretKey {
        // Проверяем, есть ли уже ключ
        val existingKey = keyStore.getKey(KEY_ALIAS, null)
        if (existingKey != null && existingKey is SecretKey) {
            return existingKey
        }

        // Создаём новый ключ
        return createNewKey()
    }

    /**
     * Создаёт новый AES ключ в защищённом хранилище
     */
    private fun createNewKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
}
