package com.kollectivemobile.euki.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.kollectivemobile.euki.App
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.manager.DeviceAuthCallback
import com.kollectivemobile.euki.manager.DeviceAuthManager
import com.kollectivemobile.euki.ui.common.BaseFragment
import com.kollectivemobile.euki.databinding.FragmentDeviceAuthBinding
import javax.inject.Inject

class DeviceAuthFragment : BaseFragment(), DeviceAuthCallback {

  @Inject lateinit var deviceAuthManager: DeviceAuthManager
  private var binding: FragmentDeviceAuthBinding? = null

  private lateinit var btnTest: Button
  private lateinit var tvStatus: TextView

  companion object {
    fun newInstance() = DeviceAuthFragment()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (activity != null) {
      (requireActivity().application as App).appComponent.inject(this)
    }

    btnTest = view.findViewById(R.id.btn_test)
    tvStatus = view.findViewById(R.id.tv_status)

    btnTest.setOnClickListener { testAuth() }
  }

  override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentDeviceAuthBinding.inflate(inflater, container, false)
    return binding!!
  }

  override fun onCreateViewCalled(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_pin_code, container, false)
  }

  private fun testAuth() {
    activity?.let { deviceAuthManager.authenticateUser(it, "Test", "Testing", this) }
  }

  override fun onAuthenticationSucceeded() {
    tvStatus.text = "SUCCESS!"
  }

  override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
    tvStatus.text = "ERROR: $errorMessage"
  }

  override fun onAuthenticationFailed() {
    tvStatus.text = "FAILED"
  }
}
