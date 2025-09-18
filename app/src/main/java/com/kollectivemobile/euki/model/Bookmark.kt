package com.kollectivemobile.euki.model

public class Bookmark {
  private var mId: String
  private var mTitle: String
  private var mContent: String
  private var mContentItem: ContentItem

  constructor() {
    this.mId = ""
    this.mTitle = ""
    this.mContent = ""
    this.mContentItem = ContentItem()
  }

  constructor(id: String, title: String, content: String, contentItem: ContentItem) {
    this.mId = id
    this.mTitle = title
    this.mContent = content
    this.mContentItem = contentItem
  }

  fun getId(): String {
    return mId
  }

  fun setId(id: String) {
    mId = id
  }

  fun getTitle(): String {
    return mTitle
  }

  fun setTitle(title: String) {
    mTitle = title
  }

  fun getContent(): String {
    return mContent
  }

  fun setContent(content: String) {
    mContent = content
  }

  fun getContentItem(): ContentItem {
    return mContentItem
  }

  fun setContentItem(contentItem: ContentItem) {
    mContentItem = contentItem
  }
}
