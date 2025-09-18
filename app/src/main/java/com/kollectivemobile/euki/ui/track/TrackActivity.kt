package com.kollectivemobile.euki.ui.track

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.model.database.entity.CalendarItem
import com.kollectivemobile.euki.ui.common.BaseActivity
import java.util.Date

class TrackActivity : BaseActivity() {
  companion object {
    const val DATE_KEY = "DATE_KEY"
    const val CALENDAR_ITEM_KEY = "CALENDAR_ITEM_KEY"

    @JvmStatic
    fun makeIntent(context: Context, date: Date?, calendarItem: CalendarItem?): Intent {
      val intent = Intent(context, TrackActivity::class.java)
      val bundle = Bundle()

      if (calendarItem != null) {
        bundle.putString(DATE_KEY, Gson().toJson(date))
        bundle.putString(CALENDAR_ITEM_KEY, Gson().toJson(calendarItem))
      }
      intent.putExtras(bundle)
      return intent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.app_bar_main)

    if (savedInstanceState == null) {
      var date: Date? = null
      intent.getStringExtra(DATE_KEY)?.let { dateValue ->
        date = Gson().fromJson(dateValue, object : TypeToken<Date>() {}.type)
      }

      var calendarItem: CalendarItem? = null
      intent.getStringExtra(CALENDAR_ITEM_KEY)?.let { itemValue ->
        calendarItem = Gson().fromJson(itemValue, object : TypeToken<CalendarItem>() {}.type)
      }

      replaceFragment(TrackFragment.newInstance(date, calendarItem), false)
    }
  }
}
