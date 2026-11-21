package com.example.data

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val TAG = "BiometricSecurity"
private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val KEY_ALIAS = "smartlabs_biometric_vault_key"

enum class BiometricCapability {
    AVAILABLE,
    NOT_ENROLLED,
    UNAVAILABLE
}

object BiometricSecurityManager {

    private val allowedAuthenticators: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        }

    fun checkCapability(context: Context): BiometricCapability {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(allowedAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricCapability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricCapability.NOT_ENROLLED
            else -> BiometricCapability.UNAVAILABLE
        }
    }

    /**
     * Generates or retrieves a hardware-backed AES-256 GCM key in AndroidKeyStore
     * for verifying biometric session integrity when BIOMETRIC_STRONG is available.
     */
    private fun getOrCreateVaultCipher(): Cipher? {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
                val builder = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    builder.setUserAuthenticationParameters(
                        0,
                        KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                    )
                }
                keyGen.init(builder.build())
                keyGen.generateKey()
            }
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey ?: return null
            Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Hardware Keystore CryptoObject unavailable; using standard BiometricPrompt", e)
            null
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String = "Unlock SmartLabs Vault",
        subtitle: String = "Verify with fingerprint, face, or device PIN to protect your study data",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val capability = checkCapability(activity)
        if (capability == BiometricCapability.UNAVAILABLE) {
            onError("Biometric or device credential security is unavailable on this device.")
            return
        }
        if (capability == BiometricCapability.NOT_ENROLLED) {
            onError("No fingerprint, face, or device lock is enrolled in Android Settings.")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Biometric did not match. Try again.")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(allowedAuthenticators)
            .setConfirmationRequired(false)
            .build()

        val cipher = getOrCreateVaultCipher()
        if (cipher != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            } catch (e: Exception) {
                Log.w(TAG, "CryptoObject auth fallback to promptInfo", e)
                prompt.authenticate(promptInfo)
            }
        } else {
            prompt.authenticate(promptInfo)
        }
    }
}
