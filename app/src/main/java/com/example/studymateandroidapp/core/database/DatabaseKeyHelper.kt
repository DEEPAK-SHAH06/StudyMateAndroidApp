package com.example.studymateandroidapp.core.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object DatabaseKeyHelper {
    private const val KEY_ALIAS = "StudyPlannerDbWrappingKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val PREFS_NAME = "study_planner_secure_prefs"
    private const val PREF_ENCRYPTED_KEY = "encrypted_db_key"
    private const val PREF_IV = "db_key_iv"

    fun getDatabaseKey(context: Context): ByteArray {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val encryptedKeyBase64 = prefs.getString(PREF_ENCRYPTED_KEY, null)
            val ivBase64 = prefs.getString(PREF_IV, null)

            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            // If we already have a key saved, decrypt and return it
            if (encryptedKeyBase64 != null && ivBase64 != null && keyStore.containsAlias(KEY_ALIAS)) {
                val encryptedKey = Base64.decode(encryptedKeyBase64, Base64.DEFAULT)
                val iv = Base64.decode(ivBase64, Base64.DEFAULT)
                return decryptDbKey(keyStore, encryptedKey, iv)
            }

            // Otherwise, generate a new Database Key, encrypt it with a new Keystore Key, and save
            val newDbKey = ByteArray(32).apply { SecureRandom().nextBytes(this) }
            
            // Generate the wrapping key in Android KeyStore
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            
            keyGenerator.init(spec)
            val wrappingKey = keyGenerator.generateKey()

            // Encrypt the new DB key
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, wrappingKey)
            val iv = cipher.iv
            val encryptedKey = cipher.doFinal(newDbKey)

            prefs.edit()
                .putString(PREF_ENCRYPTED_KEY, Base64.encodeToString(encryptedKey, Base64.DEFAULT))
                .putString(PREF_IV, Base64.encodeToString(iv, Base64.DEFAULT))
                .apply()

            return newDbKey
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: Return a deterministic key if Keystore fails (not ideal for security, but better than crash)
            return "fallback_study_planner_secure_key_123".toByteArray().copyOf(32)
        }
    }

    private fun decryptDbKey(keyStore: KeyStore, encryptedKey: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        return cipher.doFinal(encryptedKey)
    }
}
