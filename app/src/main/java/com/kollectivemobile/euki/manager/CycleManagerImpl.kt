package com.kollectivemobile.euki.manager

import com.kollectivemobile.euki.manager.converter.CycleDayItemConverter
import com.kollectivemobile.euki.manager.converter.CyclePeriodDataConverter
import com.kollectivemobile.euki.model.CycleDayItem
import com.kollectivemobile.euki.model.CyclePeriodData
import com.kollectivemobile.euki.model.CyclePeriodItem
import com.kollectivemobile.euki.model.database.entity.CalendarItem
import com.kollectivemobile.euki.networking.EukiCallback
import com.kollectivemobile.euki.networking.ServerError
import com.kollectivemobile.euki.utils.DateUtils
import java.util.*
import org.apache.commons.lang3.Range

class CycleManagerImpl(
        private val mAppSettingsManager: AppSettingsManager,
        private val mCalendarManager: CalendarManager
) : CycleManager {
  override fun requestCycleItems(callback: EukiCallback<List<CycleDayItem>>) {
    val trackPeriodEnabled = mAppSettingsManager.trackPeriodEnabled()

    mCalendarManager.getDayscalendarItems(
            object : EukiCallback<List<CalendarItem>> {
              override fun onSuccess(calendarItems: List<CalendarItem>) {
                val dict = HashMap<Date, CalendarItem>()
                for (item in calendarItems) {
                  dict[DateUtils.startDate(item.date)] = item
                }

                mCalendarManager.getPredictionRange(
                        object : EukiCallback<List<Range<Date>>> {
                          override fun onSuccess(ranges: List<Range<Date>>) {
                            callback.onSuccess(
                                    CycleDayItemConverter.convert(dict, ranges, trackPeriodEnabled)
                            )
                          }

                          override fun onError(serverError: ServerError) {
                            callback.onError(serverError)
                          }
                        }
                )
              }

              override fun onError(serverError: ServerError) {
                callback.onError(serverError)
              }
            }
    )
  }

  override fun requestCyclePeriodData(callback: EukiCallback<CyclePeriodData>) {
    if (!mAppSettingsManager.trackPeriodEnabled()) {
      callback.onSuccess(CyclePeriodData(null, null, null, null, null, ArrayList()))
      return
    }

    mCalendarManager.getDayscalendarItems(
            object : EukiCallback<List<CalendarItem>> {
              override fun onSuccess(calendarItems: List<CalendarItem>) {
                val hiddenPeriods = mAppSettingsManager.hiddenCyclePeriods()

                Collections.sort(calendarItems) { item0, item1 -> item0.date.compareTo(item1.date) }
                callback.onSuccess(CyclePeriodDataConverter.convert(calendarItems, hiddenPeriods))
              }

              override fun onError(serverError: ServerError) {
                callback.onError(serverError)
              }
            }
    )
  }

  override fun deletePeriod(item: CyclePeriodItem, callback: EukiCallback<Boolean>) {
    val hiddenPeriods = mAppSettingsManager.hiddenCyclePeriods().toMutableList()
    hiddenPeriods.add(Range.of(item.initialDate, item.endDate))
    mAppSettingsManager.saveHiddenCyclePeriods(hiddenPeriods)
    callback.onSuccess(true)
  }
}
