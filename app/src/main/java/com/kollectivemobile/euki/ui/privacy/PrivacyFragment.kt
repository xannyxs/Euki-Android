package com.kollectivemobile.euki.ui.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.viewbinding.ViewBinding
import com.kollectivemobile.euki.App
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.databinding.FragmentPrivacyBinding
import com.kollectivemobile.euki.manager.AppSettingsManager
import com.kollectivemobile.euki.manager.PrivacyContentManager
import com.kollectivemobile.euki.manager.PrivacyManager
import com.kollectivemobile.euki.ui.auth.DeviceAuthFragment
import com.kollectivemobile.euki.ui.common.BaseFragment
import com.kollectivemobile.euki.ui.common.Dialogs
import com.kollectivemobile.euki.ui.common.views.CardButtonView
import com.kollectivemobile.euki.ui.common.views.CardMaterialSwitchView
import com.kollectivemobile.euki.utils.Constants
import javax.inject.Inject

class PrivacyFragment : BaseFragment() {
  @Inject lateinit var privacyContentManager: PrivacyContentManager
  @Inject lateinit var privacyManager: PrivacyManager
  @Inject lateinit var appSettingsManager: AppSettingsManager

  private var binding: FragmentPrivacyBinding? = null
  private val recurringCounter = 4
  private var isFirstTime = true

  companion object {
    fun newInstance(): PrivacyFragment {
      val args = Bundle()
      val fragment = PrivacyFragment()
      fragment.arguments = args
      return fragment
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activity?.let { (it.application as App).appComponent.inject(this) }
    setUIElements()
    createCardButtons()
  }

  override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentPrivacyBinding.inflate(inflater, container, false)
    return binding!!
  }

  override fun onCreateViewCalled(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_privacy, container, false)
  }

    private fun createCardButtons() {
        val container =
            binding?.root?.findViewById(R.id.container_buttons)
                ?: LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    binding?.root?.addView(this, 0)
                }

        val switch =
            listOf(
                "Device Authentication" to { navigateToDeviceAuth() },
            )

        switch.forEach { (title, clickAction) ->
            val cardButton =
                CardMaterialSwitchView(requireContext()).apply {
                    setTitle(title)
                    setOnClickListener { clickAction() }
                }
            container.addView(cardButton)
        }

        val buttons =
            listOf(
                "Privacy Guide" to { showPrivacyBestPractices() },
                "Privacy FAQs" to { showPrivacyFaqs() },
            )

        buttons.forEach { (title, clickAction) ->
            val cardButton =
                CardButtonView(requireContext()).apply {
                    setTitle(title)
                    setOnClickListener { clickAction() }
                }
            container.addView(cardButton)
        }
    }

  private fun navigateToDeviceAuth() {
    val deviceAuthFragment = DeviceAuthFragment()
    mInteractionListener?.replaceFragment(deviceAuthFragment, true)
  }

  private fun setUIElements() {
    binding?.apply {
      dbRecurring.max = recurringCounter
      dbRecurring.incrementProgressBy(1)
      dbRecurring.setOnSeekBarChangeListener(onSeekBarChangeListener)
      swchRecurring.setOnCheckedChangeListener(onCheckedChangeListener)
      isFirstTime = privacyManager.recurringType != null

      bDeletteAllData.setOnClickListener { deleteAllData() }
    }

    updateUIElements()
  }

  private fun deleteAllData() {
    Dialogs.createSimpleDialog(
                    activity,
                    null,
                    getString(R.string.confirm_delete_all_now),
                    getString(R.string.ok),
                    true
            ) { _, _ -> privacyManager.removeAllData() }
            .show()
  }

  private fun showPrivacyFaqs() {
    showContentItem(privacyContentManager.privacyFAQs)
  }

  private fun showPrivacyBestPractices() {
    showContentItem(privacyContentManager.privacyBestPractices)
  }

  private fun updateUIElements() {
    binding?.apply {
      val recurringType = privacyManager.recurringType
      tvRecurring.text = getTitle(recurringType)
      swchRecurring.isChecked = recurringType != null
      dbRecurring.progress = recurringType?.ordinal ?: 0
      llRecurringContainer.visibility = if (swchRecurring.isChecked) View.VISIBLE else View.GONE
    }
  }

  private fun getTitle(index: Int): String {
    val recurringType = Constants.DeleteRecurringType.values[index]
    return getTitle(recurringType)
  }

  private fun getTitle(type: Constants.DeleteRecurringType?): String {
    return type?.let { getString(it.textRestId) } ?: ""
  }

  private val onSeekBarChangeListener =
          object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
              if (fromUser) {
                binding?.tvRecurring?.text = getTitle(progress)
                val recurringType = Constants.DeleteRecurringType.values[progress]
                if (recurringType != privacyManager.recurringType) {
                  privacyManager.saveRecurringData(recurringType)
                }
              }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
          }

  private val onCheckedChangeListener =
          CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isFirstTime) {
              isFirstTime = false
              return@OnCheckedChangeListener
            }

            if (isChecked) {
              Dialogs.createTwoOptionsDialog(
                              activity,
                              null,
                              getString(R.string.weekly_recurring_info),
                              getString(R.string.ok),
                              getString(R.string.cancel),
                              { _, _ ->
                                privacyManager.saveRecurringData(
                                        Constants.DeleteRecurringType.WEEKLY
                                )
                                updateUIElements()
                              },
                              { _, _ -> updateUIElements() }
                      )
                      .show()
            } else {
              privacyManager.saveRecurringData(null)
              updateUIElements()
            }
          }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
