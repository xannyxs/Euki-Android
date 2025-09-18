package com.kollectivemobile.euki.manager

import com.kollectivemobile.euki.model.CycleDayItem
import com.kollectivemobile.euki.model.CyclePeriodData
import com.kollectivemobile.euki.model.CyclePeriodItem
import com.kollectivemobile.euki.networking.EukiCallback

interface CycleManager {
  fun requestCycleItems(callback: EukiCallback<List<CycleDayItem>>)
  fun requestCyclePeriodData(callback: EukiCallback<CyclePeriodData>)
  fun deletePeriod(item: CyclePeriodItem, callback: EukiCallback<Boolean>)
}
