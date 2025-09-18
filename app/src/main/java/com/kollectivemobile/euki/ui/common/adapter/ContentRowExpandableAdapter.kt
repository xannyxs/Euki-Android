package com.kollectivemobile.euki.ui.common.adapter

import android.content.Context
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.listeners.LinkListener
import com.kollectivemobile.euki.model.ContentItem
import com.kollectivemobile.euki.model.SwipeableItem
import com.kollectivemobile.euki.utils.TextUtils
import com.kollectivemobile.euki.utils.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.kollectivemobile.euki.utils.advrecyclerview.utils.AbstractExpandableItemViewHolder
import com.kollectivemobile.euki.utils.siwperview.CustomContainerView
import java.lang.ref.WeakReference

class ContentRowExpandableAdapter(
        context: Context,
        items: List<ContentItem>,
        private val mListener: ContentRowExpandableListener?,
        private val mLinkListener: LinkListener?
) :
        AbstractExpandableItemAdapter<
                ContentRowExpandableAdapter.TitleHolder, ContentRowExpandableAdapter.TextHolder>() {

  private val mContentItems: MutableList<ContentItem> = ArrayList()
  private val mContext: WeakReference<Context> = WeakReference(context)
  private val mSwiperItems: List<SwipeableItem> = emptyList()

  init {
    mContentItems.addAll(items)
    setHasStableIds(true)
  }

  override fun getGroupCount(): Int = mContentItems.size

  override fun getChildCount(groupPosition: Int): Int = 1

  override fun getGroupId(groupPosition: Int): Long = mContentItems[groupPosition].itemId

  override fun getChildId(groupPosition: Int, childPosition: Int): Long =
          mContentItems[groupPosition].itemId

  override fun getGroupItemViewType(groupPosition: Int): Int = 0

  override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int = 0

  override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): TitleHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.layout_content_row_expandable_title, parent, false)
    return TitleHolder(view, mListener)
  }

  override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): TextHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.layout_content_row_expandable_text, parent, false)
    return TextHolder(view)
  }

  override fun onBindGroupViewHolder(holder: TitleHolder, groupPosition: Int, viewType: Int) {
    val contentItem = mContentItems[groupPosition]

    holder.tvTitle.text = contentItem.localizedTitle
    holder.vSeparatorTop.visibility = View.VISIBLE
    holder.vSeparatorBottom.visibility =
            if (groupPosition == mContentItems.size - 1) View.VISIBLE else View.GONE

    val expandState = holder.expandState

    if (expandState.isUpdated) {
      holder.ivArrow.setImageResource(
              if (expandState.isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
      )
      holder.vSeparatorBottom.visibility =
              when {
                expandState.isExpanded -> View.GONE
                groupPosition == mContentItems.size - 1 -> View.VISIBLE
                else -> View.GONE
              }
    }
  }

  override fun onBindChildViewHolder(
          holder: TextHolder,
          groupPosition: Int,
          childPosition: Int,
          viewType: Int
  ) {
    val contentItem = mContentItems[groupPosition]

    val originalText = contentItem.localizedContent
    val spannedFormattedText = Html.fromHtml(originalText, Html.FROM_HTML_MODE_LEGACY)
    val spannableFormattedText = SpannableString(spannedFormattedText)
    val linkSpannable =
            TextUtils.getSpannable(
                    originalText,
                    contentItem.links,
                    mLinkListener,
                    contentItem.boldStrings
            )

    for (span in linkSpannable.getSpans(0, linkSpannable.length, Any::class.java)) {
      val start = linkSpannable.getSpanStart(span)
      val end = linkSpannable.getSpanEnd(span)

      val spanText = linkSpannable.subSequence(start, end).toString()

      val newStart = spannedFormattedText.toString().indexOf(spanText)
      val newEnd = newStart + spanText.length

      if (newStart >= 0 && newEnd <= spannedFormattedText.length) {
        spannableFormattedText.setSpan(span, newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
    }

    holder.tvTitle.text = spannableFormattedText
    holder.tvTitle.movementMethod = LinkMovementMethod.getInstance()
    holder.vSeparatorBottom.visibility =
            if (groupPosition == mContentItems.size - 1) View.VISIBLE else View.GONE

    // Check if the contentItem has swiper items
    contentItem.swiperItems?.let { swiperItems ->
      if (swiperItems.isNotEmpty()) {
        holder.swiperView.setItems(swiperItems)
        holder.swiperView.visibility = View.VISIBLE
      } else {
        holder.swiperView.visibility = View.GONE
      }
    }
            ?: run { holder.swiperView.visibility = View.GONE }
  }

  override fun onCheckCanExpandOrCollapseGroup(
          holder: TitleHolder,
          groupPosition: Int,
          x: Int,
          y: Int,
          expand: Boolean
  ): Boolean = true

  class TitleHolder(itemView: View, private val mListener: ContentRowExpandableListener?) :
          AbstractExpandableItemViewHolder(itemView) {

    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)
    val vSeparatorTop: View = itemView.findViewById(R.id.v_separator_top)
    val vSeparatorBottom: View = itemView.findViewById(R.id.v_separator_bottom)

    init {
      itemView.findViewById<View>(R.id.rl_main).setOnClickListener { onClick() }
    }

    private fun onClick() {
      mListener?.rowExpandableSelected(layoutPosition - 1)
    }
  }

  class TextHolder(itemView: View) : AbstractExpandableItemViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val vSeparatorBottom: View = itemView.findViewById(R.id.v_separator_bottom)
    val swiperView: CustomContainerView = itemView.findViewById(R.id.swiper_view)
  }

  interface ContentRowExpandableListener {
    fun rowExpandableSelected(position: Int)
  }
}
