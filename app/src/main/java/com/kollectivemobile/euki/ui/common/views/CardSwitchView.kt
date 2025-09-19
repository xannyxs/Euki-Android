package com.kollectivemobile.euki.ui.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.kollectivemobile.euki.R

class CardMaterialSwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val titleTextView: TextView
    private val materialSwitch: MaterialSwitch
    private val cardView: MaterialCardView

    init {
        LayoutInflater.from(context).inflate(R.layout.card_switch_view, this, true)

        titleTextView = findViewById(R.id.tv_title)
        materialSwitch = findViewById(R.id.switch_control) // Updated ID
        cardView = findViewById(R.id.card_view)

        cardView.isClickable = true
        cardView.isFocusable = true
        cardView.setOnClickListener {
            materialSwitch.toggle()
        }
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setChecked(checked: Boolean) {
        materialSwitch.isChecked = checked
    }

    fun isChecked(): Boolean {
        return materialSwitch.isChecked
    }

    fun setOnSwitchChangeListener(listener: ((isChecked: Boolean) -> Unit)?) {
        if (listener == null) {
            materialSwitch.setOnCheckedChangeListener(null)
        } else {
            materialSwitch.setOnCheckedChangeListener { _, isChecked ->
                listener(isChecked)
            }
        }
    }
}
