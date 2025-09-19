package com.kollectivemobile.euki.manager

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class DeviceAuthManagerImpl(private val context: Context) : DeviceAuthManager {

  override fun isDeviceAuthAvailable(): DeviceAuthAvailability {
    val biometricManager = BiometricManager.from(context)

    return when (biometricManager.canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
    ) {
      BiometricManager.BIOMETRIC_SUCCESS -> DeviceAuthAvailability.AVAILABLE
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> DeviceAuthAvailability.NO_HARDWARE
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> DeviceAuthAvailability.HARDWARE_UNAVAILABLE
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> DeviceAuthAvailability.NO_ENROLLMENT
      else -> DeviceAuthAvailability.UNKNOWN
    }
  }

  override fun authenticateUser(
          fragmentActivity: FragmentActivity,
          title: String,
          subtitle: String,
          callback: DeviceAuthCallback
  ) {
    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt =
            BiometricPrompt(
                    fragmentActivity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        callback.onAuthenticationError(errorCode, errString.toString())
                      }

                      override fun onAuthenticationSucceeded(
                              result: BiometricPrompt.AuthenticationResult
                      ) {
                        super.onAuthenticationSucceeded(result)
                        callback.onAuthenticationSucceeded()
                      }

                      override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        callback.onAuthenticationFailed()
                      }
                    }
            )

    val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setAllowedAuthenticators(
                            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()

    biometricPrompt.authenticate(promptInfo)
  }
}
