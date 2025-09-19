package com.kollectivemobile.euki.ui.privacy

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.kollectivemobile.euki.App
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.databinding.FragmentPrivacyPinCodeBinding
import com.kollectivemobile.euki.manager.AppSettingsManager
import com.kollectivemobile.euki.ui.onboarding.PinSetupFragment
import com.kollectivemobile.euki.utils.Utils
import javax.inject.Inject

class PrivacyPinSetupFragment : PinSetupFragment() {
    private enum class PinCodeType {
        CREATE,
        CONFIRM
    }

    @Inject
    lateinit var mAppSettingsManager: AppSettingsManager

    private var binding: FragmentPrivacyPinCodeBinding? = null
    private var mPinCodeType = PinCodeType.CREATE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyPinCodeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreateViewCalled(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy_pin_code, container, false)
    }

    override fun onResume() {
        super.onResume()
        binding?.tvError?.visibility = View.GONE
    }

    override fun initInjection() {
        (activity?.application as? App)?.appComponent?.inject(this)
    }

    override fun setUIElements() {
        super.setUIElements()
        mPinCodeType =
            if (mAppSettingsManager.pinCode == null) PinCodeType.CREATE else PinCodeType.CONFIRM
        updateCodeType()
        showDots()
    }

    private fun updateCodeType() {
        val titleResId: Int = when (mPinCodeType) {
            PinCodeType.CREATE ->
                if (mAppSettingsManager.pinCode == null) R.string.onboarding_set_pin_title
                else R.string.set_new_pin

            PinCodeType.CONFIRM -> R.string.confirm_existing_pin
        }
        binding?.tvTitle?.setText(getString(titleResId))
        binding?.tvTitle?.gravity =
            if (titleResId == R.string.onboarding_set_pin_title) Gravity.LEFT else Gravity.CENTER_HORIZONTAL
    }

    override fun showDots() {
        super.showDots()
        val isCompleted = code.length == 4

        if (mPinCodeType == PinCodeType.CREATE && mAppSettingsManager.pinCode != null) {
            binding?.btnSetPin?.alpha = if (isCompleted) 1.0f else 0.5f
            binding?.btnSetPin?.isEnabled = isCompleted
            binding?.btnSetPin?.text = getString(R.string.set_new_pin_button).uppercase()
        }

        if (mPinCodeType == PinCodeType.CONFIRM) {
            binding?.btnSetPin?.alpha = if (isCompleted) 1.0f else 0.5f
            binding?.btnSetPin?.isEnabled = isCompleted
            binding?.btnSetPin?.text = getString(R.string.next).uppercase()

            val codeLength = code.length

            tvDigits?.forEach { textView ->
                val number = Utils.parseInt(textView.tag.toString())
                val digit = if (codeLength > number - 1) "*" else ""
                textView.text = digit
            }

            vDigits?.forEach { lineView ->
                val colorRes = if (codeLength > 0) R.color.blueberry else R.color.blueberry
                lineView.setBackgroundColor(ContextCompat.getColor(App.getContext(), colorRes))
            }
        }
    }

    override fun setPin() {
        if (mPinCodeType == PinCodeType.CONFIRM) {
            val currentPin = mAppSettingsManager.pinCode
            if (currentPin != null && currentPin == code) {
                binding?.tvError?.visibility = View.GONE
                code = ""
                mPinCodeType = PinCodeType.CREATE
                updateCodeType()
            } else {
                binding?.tvError?.visibility = View.VISIBLE
                code = ""
            }
            showDots()
        } else {
            val message =
                if (code.isEmpty()) R.string.new_pin_skip else R.string.new_pin_confirmation
            Toast.makeText(activity, getString(message), Toast.LENGTH_LONG).show()

            if (code.length == 4) {
                mAppSettingsManager.savePinCode(code)
            }
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // Avoid memory leaks
    }

    companion object {
        fun newInstance(): PrivacyPinSetupFragment {
            val args = Bundle()
            val fragment = PrivacyPinSetupFragment()
            fragment.arguments = args
            return fragment
        }
    }
}