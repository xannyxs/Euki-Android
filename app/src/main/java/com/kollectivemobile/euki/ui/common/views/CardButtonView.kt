package com.kollectivemobile.euki.ui.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.kollectivemobile.euki.R

class CardButtonView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val titleTextView: TextView
  private val arrowImageView: ImageView
  private val cardView: MaterialCardView

  init {
    LayoutInflater.from(context).inflate(R.layout.card_button_view, this, true)

    titleTextView = findViewById(R.id.tv_title)
    arrowImageView = findViewById(R.id.iv_arrow)
    cardView = findViewById(R.id.card_view)

    cardView.isClickable = true
    cardView.isFocusable = true
  }

  fun setTitle(title: String) {
    titleTextView.text = title
  }

  override fun setOnClickListener(listener: OnClickListener?) {
    cardView.setOnClickListener(listener)
  }
}