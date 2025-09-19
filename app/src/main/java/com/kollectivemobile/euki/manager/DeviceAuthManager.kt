package com.kollectivemobile.euki.manager

import androidx.fragment.app.FragmentActivity

enum class DeviceAuthAvailability {
  AVAILABLE,
  NO_HARDWARE,
  HARDWARE_UNAVAILABLE,
  NO_ENROLLMENT,
  UNKNOWN
}

interface DeviceAuthManager {
  fun isDeviceAuthAvailable(): DeviceAuthAvailability
  fun authenticateUser(
          fragmentActivity: FragmentActivity,
          title: String,
          subtitle: String,
          callback: DeviceAuthCallback
  )
}

interface DeviceAuthCallback {
  fun onAuthenticationSucceeded()
  fun onAuthenticationError(errorCode: Int, errorMessage: String)
  fun onAuthenticationFailed()
}
