package com.kollectivemobile.euki.manager

import com.kollectivemobile.euki.model.ContentItem
import com.kollectivemobile.euki.networking.EukiCallback

interface ContentManager {
  fun getAbortionKnowledge(callback: EukiCallback<ContentItem>)
  fun getAbortionWalkthrough(callback: EukiCallback<ContentItem>)
  fun getSTIs(callback: EukiCallback<ContentItem>)
  fun getContraception(callback: EukiCallback<ContentItem>)
  fun getMiscarriage(callback: EukiCallback<ContentItem>)
  fun getSexRelationships(callback: EukiCallback<ContentItem>)
  fun getPregnancyOptions(callback: EukiCallback<ContentItem>)
  fun getMenstruationOptions(callback: EukiCallback<ContentItem>)

  fun getContentItem(id: String): ContentItem?
  fun getContentItem(id: String, contentItem: ContentItem): ContentItem?

  fun searchContentItem(searchText: String): List<ContentItem>
  fun searchContentItem(searchText: String, contentItem: ContentItem): List<ContentItem>
}
