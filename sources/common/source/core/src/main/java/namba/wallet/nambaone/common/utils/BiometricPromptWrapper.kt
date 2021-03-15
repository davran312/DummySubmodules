package namba.wallet.nambaone.common.utils

import androidx.annotation.StringRes
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import javax.crypto.Cipher
import namba.wallet.nambaone.core.R

class BiometricPromptWrapper(
    private val fragment: Fragment,
    @StringRes private val titleRes: Int = R.string.auth_fingerprint,
    @StringRes private val descriptionRes: Int = R.string.auth_fingerprint_info,
    @StringRes private val negativeRes: Int = R.string.cancel,
    private val onError: ((CharSequence?) -> Unit)? = null,
    private val onSuccess: (Cipher) -> Unit
) {

    private val biometricCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            if (errorCode == BiometricConstants.ERROR_CANCELED) {
                fragment.lifecycleScope.launchWhenStarted {
                    if (!fragment.isDetached)
                        authenticateActual()
                }
                return
            }

            onError?.invoke(
                errString.takeIf {
                    errorCode != BiometricConstants.ERROR_USER_CANCELED &&
                        errorCode != BiometricConstants.ERROR_NEGATIVE_BUTTON
                })
        }

        override fun onAuthenticationFailed() {
            authenticateActual()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess(result.cryptoObject!!.cipher!!)
        }
    }

    private lateinit var cipher: Cipher

    private val biometricPrompt = BiometricPrompt(
        fragment,
        ContextCompat.getMainExecutor(fragment.requireContext()),
        biometricCallback
    )

    private val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(fragment.getString(titleRes))
        .setDescription(fragment.getString(descriptionRes))
        .setNegativeButtonText(fragment.getString(negativeRes))
        .build()

    fun authenticate(cipher: Cipher) {
        this.cipher = cipher
        authenticateActual()
    }

    private fun authenticateActual() {
        biometricPrompt.authenticate(biometricPromptInfo, CryptoObject(cipher))
    }
}
