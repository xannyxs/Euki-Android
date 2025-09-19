package com.kollectivemobile.euki.ui.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.ui.common.BaseActivity

class PrivacyPinSetupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_main)
        if (savedInstanceState == null) {
            replaceFragment(PrivacyPinSetupFragment.newInstance(), false)
        }
    }

    companion object {
        fun makeIntent(context: Context): Intent {
            return Intent(context, PrivacyPinSetupActivity::class.java)
        }
    }
}