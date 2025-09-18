package com.kollectivemobile.euki.manager

import com.kollectivemobile.euki.model.ContentItem
import com.kollectivemobile.euki.networking.EukiCallback
import com.kollectivemobile.euki.utils.Utils

class ContentManagerImpl(
        private val mAbortionContentManager: AbortionContentManager,
        private val mPrivacyContentManager: PrivacyContentManager
) : ContentManager {

  companion object {
    private const val ABORTION_KNOWLEDGE_JSON = "abortion_knowledge"
    private const val ABORTION_WALKTHROUGH_JSON = "abortion_walkthrough"
    private const val STIS_JSON = "stis"
    private const val CONTRACEPTION_JSON = "contraception"
    private const val MISCARRIAGE_JSON = "miscarriage"
    private const val SEX_RELATIONSHIPS_JSON = "sex_relationships"
    private const val PREGNANCY_OPTIONS = "pregnancy_options"
    private const val MENSTRUATION_OPTIONS = "menstruation_options"
  }

  private var mAbortionKnowledge: ContentItem? = null
  private var mAbortionWalkthrough: ContentItem? = null
  private var mSTI: ContentItem? = null
  private var mContraception: ContentItem? = null
  private var mMiscarriage: ContentItem? = null
  private var mSexRelationships: ContentItem? = null
  private var mPregnancyOptions: ContentItem? = null
  private var mMenstruationOptions: ContentItem? = null

  override fun getAbortionKnowledge(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getAbortionKnowledge())
  }

  override fun getAbortionWalkthrough(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getAbortionWalkthrough())
  }

  override fun getSTIs(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getSTI())
  }

  override fun getContraception(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getContraception())
  }

  override fun getMiscarriage(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getMiscarriage())
  }

  override fun getSexRelationships(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getSexRelationships())
  }

  override fun getPregnancyOptions(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getPregnancyOptions())
  }

  override fun getMenstruationOptions(callback: EukiCallback<ContentItem>) {
    callback.onSuccess(getMenstruationOptions())
  }

  override fun getContentItem(id: String): ContentItem? {
    // Search through main content items
    val mainContentItems =
            listOf(
                    getAbortionKnowledge(),
                    getAbortionWalkthrough(),
                    getSTI(),
                    getContraception(),
                    getMiscarriage(),
                    getSexRelationships(),
                    getPregnancyOptions(),
                    getMenstruationOptions()
            )

    for (contentItem in mainContentItems) {
      val foundContentItem = getContentItem(id, contentItem)
      if (foundContentItem != null) {
        return foundContentItem
      }
    }

    // Search through abortion content items
    val abortionContentItems =
            listOf(
                    mAbortionContentManager.abortionMifeMiso12,
                    mAbortionContentManager.abortionMiso12,
                    mAbortionContentManager.abortionSuctionVacuum,
                    mAbortionContentManager.abortionDilationEvacuation,
                    mAbortionContentManager.medialAbortion
            )

    for (contentItem in abortionContentItems) {
      val foundContentItem = getContentItem(id, contentItem)
      if (foundContentItem != null) {
        return foundContentItem
      }
    }

    // Search through privacy content items
    val privacyContentItems =
            listOf(
                    mPrivacyContentManager.privacyFAQs,
                    mPrivacyContentManager.privacyStatement,
                    mPrivacyContentManager.privacyBestPractices
            )

    for (contentItem in privacyContentItems) {
      val foundContentItem = getContentItem(id, contentItem)
      if (foundContentItem != null) {
        return foundContentItem
      }
    }

    return null
  }

  override fun getContentItem(id: String, contentItem: ContentItem): ContentItem? {
    if (contentItem.id == null) {
      return null
    }

    if (contentItem.id == id) {
      return contentItem
    }

    // Search in content items
    contentItem.contentItems?.forEach { childItem ->
      val foundItem = getContentItem(id, childItem)
      if (foundItem != null) {
        return foundItem
      }
    }

    // Search in expandable items
    contentItem.expandableItems?.forEach { childItem ->
      val foundItem = getContentItem(id, childItem)
      if (foundItem != null) {
        foundItem.parent = contentItem
        return foundItem
      }
    }

    // Search in selectable items
    contentItem.selectableItems?.forEach { childItem ->
      val foundItem = getContentItem(id, childItem)
      if (foundItem != null) {
        return foundItem
      }
    }

    // Search in selectable row items
    contentItem.selectableRowItems?.forEach { childItem ->
      val foundItem = getContentItem(id, childItem)
      if (foundItem != null) {
        return foundItem
      }
    }

    return null
  }

  override fun searchContentItem(searchText: String): List<ContentItem> {
    val items = mutableListOf<ContentItem>()

    // Search main content items
    val mainContentItems =
            listOf(
                    getAbortionKnowledge(),
                    getAbortionWalkthrough(),
                    getSTI(),
                    getContraception(),
                    getMiscarriage(),
                    getSexRelationships(),
                    getPregnancyOptions(),
                    getMenstruationOptions()
            )

    for (contentItem in mainContentItems) {
      items.addAll(searchContentItem(searchText, contentItem))
    }

    // Search abortion content items
    val abortionContentItems =
            listOf(
                    mAbortionContentManager.abortionMifeMiso12,
                    mAbortionContentManager.abortionMiso12,
                    mAbortionContentManager.abortionSuctionVacuum,
                    mAbortionContentManager.abortionDilationEvacuation,
                    mAbortionContentManager.medialAbortion
            )

    for (contentItem in abortionContentItems) {
      items.addAll(searchContentItem(searchText, contentItem))
    }

    // Remove duplicates
    return items.distinct()
  }

  override fun searchContentItem(searchText: String, contentItem: ContentItem): List<ContentItem> {
    val items = mutableListOf<ContentItem>()

    if (contentItem.id == null) {
      return items
    }

    val searchTextLower = searchText.lowercase()

    // Check if current item matches search criteria
    var matches = false

    Utils.getLocalized(contentItem.id)?.let { id ->
      if (id.lowercase().contains(searchTextLower)) {
        matches = true
      }
    }

    Utils.getLocalized(contentItem.title)?.let { title ->
      if (title.lowercase().contains(searchTextLower)) {
        matches = true
      }
    }

    Utils.getLocalized(contentItem.content)?.let { content ->
      if (content.lowercase().contains(searchTextLower)) {
        matches = true
      }
    }

    if (matches) {
      items.add(contentItem)
    }

    // Search in child items
    contentItem.contentItems?.forEach { childItem ->
      if (searchContentItem(searchText, childItem).isNotEmpty()) {
        items.add(contentItem)
      }
    }

    contentItem.expandableItems?.forEach { childItem ->
      if (searchContentItem(searchText, childItem).isNotEmpty()) {
        items.add(contentItem)
      }
    }

    contentItem.selectableItems?.forEach { childItem ->
      items.addAll(searchContentItem(searchText, childItem))
    }

    contentItem.selectableRowItems?.forEach { childItem ->
      items.addAll(searchContentItem(searchText, childItem))
    }

    // Remove duplicates
    return items.distinct()
  }

  private fun createContentItem(filename: String): ContentItem {
    val jsonObject = Utils.getJsonFromAssets(filename)
    return ContentItem(jsonObject)
  }

  /*
     Getters
  */

  private fun getAbortionKnowledge(): ContentItem {
    if (mAbortionKnowledge == null) {
      mAbortionKnowledge = createContentItem(ABORTION_KNOWLEDGE_JSON)
    }
    return mAbortionKnowledge!!
  }

  private fun getAbortionWalkthrough(): ContentItem {
    if (mAbortionWalkthrough == null) {
      mAbortionWalkthrough = createContentItem(ABORTION_WALKTHROUGH_JSON)
    }
    return mAbortionWalkthrough!!
  }

  private fun getSTI(): ContentItem {
    if (mSTI == null) {
      mSTI = createContentItem(STIS_JSON)
    }
    return mSTI!!
  }

  private fun getContraception(): ContentItem {
    if (mContraception == null) {
      mContraception = createContentItem(CONTRACEPTION_JSON)
    }
    return mContraception!!
  }

  private fun getMiscarriage(): ContentItem {
    if (mMiscarriage == null) {
      mMiscarriage = createContentItem(MISCARRIAGE_JSON)
    }
    return mMiscarriage!!
  }

  private fun getSexRelationships(): ContentItem {
    if (mSexRelationships == null) {
      mSexRelationships = createContentItem(SEX_RELATIONSHIPS_JSON)
    }
    return mSexRelationships!!
  }

  private fun getPregnancyOptions(): ContentItem {
    if (mPregnancyOptions == null) {
      mPregnancyOptions = createContentItem(PREGNANCY_OPTIONS)
    }
    return mPregnancyOptions!!
  }

  private fun getMenstruationOptions(): ContentItem {
    if (mMenstruationOptions == null) {
      mMenstruationOptions = createContentItem(MENSTRUATION_OPTIONS)
    }
    return mMenstruationOptions!!
  }
}
