package com.kollectivemobile.euki.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.databinding.FragmentTrackBinding
import com.kollectivemobile.euki.model.database.entity.CalendarItem
import com.kollectivemobile.euki.networking.EukiCallback
import com.kollectivemobile.euki.networking.ServerError
import com.kollectivemobile.euki.ui.calendar.weeklycalendar.WeeklyCalendarFragment
import com.kollectivemobile.euki.ui.calendar.weeklycalendar.WeeklyCalendarListener
import com.kollectivemobile.euki.ui.common.BaseFragment
import com.kollectivemobile.euki.ui.dailylog.DailyLogFragment
import com.kollectivemobile.euki.ui.dailylog.DailyLogListener
import java.util.Date

class TrackFragment : BaseFragment(), WeeklyCalendarListener, DailyLogListener {
  private var mDate: Date? = null
  private var mCalendarItem: CalendarItem? = null

  private var weeklyCalendarFragment: WeeklyCalendarFragment? = null
  private var dailyLogFragment: DailyLogFragment? = null
  private lateinit var binding: FragmentTrackBinding

  companion object {
    fun newInstance(date: Date?, calendarItem: CalendarItem?): TrackFragment {
      val args = Bundle()
      val fragment = TrackFragment()
      fragment.mDate = date
      fragment.mCalendarItem = calendarItem
      fragment.arguments = args

      return fragment
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUIElements()
  }

  override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentTrackBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onCreateViewCalled(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_track, container, false)
  }

  private fun setUIElements() {
    val selectedDate =
            when {
              mCalendarItem?.date != null -> mCalendarItem!!.date
              mDate != null -> mDate
              else -> Date()
            }

    val weeklyFragment = WeeklyCalendarFragment.newInstance(mDate, selectedDate, this)
    replace(R.id.fl_header, weeklyFragment)
    weeklyCalendarFragment = weeklyFragment
  }

  private fun replace(@IdRes containerId: Int, fragment: Fragment) {
    val replaceTransaction = childFragmentManager.beginTransaction()
    replaceTransaction.replace(containerId, fragment, fragment.javaClass.name)

    replaceTransaction.commit()
  }

  override fun selectedDate(calendarItem: CalendarItem) {
    dailyLogFragment?.save(
            object : EukiCallback<Boolean> {
              override fun onSuccess(result: Boolean) {
                show(calendarItem)
                reloadWeekCalendar()
              }

              override fun onError(serverError: ServerError) {
                show(calendarItem)
                reloadWeekCalendar()
              }
            }
    )
            ?: run { show(calendarItem) }
  }

  private fun reloadWeekCalendar() {
    weeklyCalendarFragment?.reloadData()
  }

  private fun show(calendarItem: CalendarItem) {
    val fragment = DailyLogFragment.newInstance(calendarItem, this)

    replace(R.id.fl_content, fragment)
    dailyLogFragment = fragment
  }

  override fun refreshFilterItems() {
    weeklyCalendarFragment?.reloadData()
  }
}
