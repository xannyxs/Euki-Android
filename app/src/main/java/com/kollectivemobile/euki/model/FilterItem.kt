package com.kollectivemobile.euki.model

import com.kollectivemobile.euki.R

class FilterItem(private var mColor: Int, private var mTitle: String, private var mIsOn: Boolean) {
  fun getColor(): Int {
    return mColor
  }

  fun setColor(color: Int) {
    mColor = color
  }

  fun getTitle(): String {
    return mTitle
  }

  fun setTitle(title: String) {
    mTitle = title
  }

  fun getOn(): Boolean {
    return mIsOn
  }

  fun setOn(on: Boolean) {
    mIsOn = on
  }

  companion object {
    @JvmStatic
    fun getAllCategories(): List<FilterItem> {
      val allCategories = mutableListOf<FilterItem>()
      allCategories.add(FilterItem(R.color.bleeding, "bleeding", true))
      allCategories.add(FilterItem(R.color.emotions, "emotions", true))
      allCategories.add(FilterItem(R.color.body, "body", true))
      allCategories.add(FilterItem(R.color.sexual_activity, "sexual_activity", true))
      allCategories.add(FilterItem(R.color.contraception, "contraception", true))
      allCategories.add(FilterItem(R.color.test, "test", true))
      allCategories.add(FilterItem(R.color.appointment, "appointment", true))
      allCategories.add(FilterItem(R.color.note, "note", true))
      return allCategories
    }
  }
}
