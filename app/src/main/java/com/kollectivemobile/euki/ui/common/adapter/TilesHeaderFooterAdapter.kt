package com.kollectivemobile.euki.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kollectivemobile.euki.App
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.model.Appointment
import com.kollectivemobile.euki.model.database.entity.ReminderItem
import com.kollectivemobile.euki.utils.advrecyclerview.headerfooter.AbstractHeaderFooterWrapperAdapter

class TilesHeaderFooterAdapter(
        adapter: RecyclerView.Adapter<*>,
        private val mHeaderListener: HeaderListener?,
        private val mFooterListener: FooterListener?
) :
        AbstractHeaderFooterWrapperAdapter<
                TilesHeaderFooterAdapter.HeaderViewHolder,
                TilesHeaderFooterAdapter.FooterViewHolder>() {

  private var mIsEditing: Boolean = false
  private var mHeaderObject: Any? = null

  init {
    setAdapter(adapter)
  }

  override fun onCreateHeaderItemViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
    val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_tile_header, parent, false)
    return HeaderViewHolder(view, mHeaderListener)
  }

  override fun onCreateFooterItemViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
    val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_tile_footer, parent, false)
    return FooterViewHolder(view, mFooterListener)
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    setupFullSpanForGridLayoutManager(recyclerView)
  }

  override fun onBindHeaderItemViewHolder(holder: HeaderViewHolder, localPosition: Int) {
    when (val headerObject = mHeaderObject) {
      is ReminderItem -> {
        holder.tvTitle.text = headerObject.title
        holder.tvText.text = headerObject.text
        holder.btnDailyLog.visibility = View.GONE
      }
      is Appointment -> {
        holder.tvTitle.text = headerObject.title
        holder.tvText.text = headerObject.location
      }
    }
  }

  override fun onBindFooterItemViewHolder(holder: FooterViewHolder, localPosition: Int) {
    val context = App.getContext()
    holder.btnEdit.text = context?.getString(if (mIsEditing) R.string.done else R.string.edit_tiles)
  }

  override fun getHeaderItemCount(): Int = if (mHeaderObject == null) 0 else 1

  override fun getFooterItemCount(): Int = 1

  fun setupFullSpanForGridLayoutManager(recyclerView: RecyclerView) {
    val lm = recyclerView.layoutManager
    if (lm !is GridLayoutManager) {
      return
    }

    val glm = lm
    val origSizeLookup = glm.spanSizeLookup
    val spanCount = glm.spanCount

    glm.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
              override fun getSpanSize(position: Int): Int {
                val segmentedPosition = getSegmentedPosition(position)
                val segment = extractSegmentPart(segmentedPosition)
                val offset = extractSegmentOffsetPart(segmentedPosition)

                return if (segment == SEGMENT_TYPE_NORMAL) {
                  origSizeLookup.getSpanSize(offset)
                } else {
                  spanCount // header or footer
                }
              }
            }
  }

  class HeaderViewHolder(itemView: View, private val mHeaderListener: HeaderListener?) :
          RecyclerView.ViewHolder(itemView) {

    val rlMain: RelativeLayout = itemView.findViewById(R.id.rl_main)
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val tvText: TextView = itemView.findViewById(R.id.tv_text)
    val btnDailyLog: Button = itemView.findViewById(R.id.btn_go_to_daily_log)

    init {
      itemView.findViewById<View>(R.id.btn_dismiss).setOnClickListener {
        mHeaderListener?.dismissPressed()
      }

      btnDailyLog.setOnClickListener { mHeaderListener?.goToDailyLogPressed() }
    }
  }

  class FooterViewHolder(itemView: View, private val mFooterListener: FooterListener?) :
          RecyclerView.ViewHolder(itemView) {

    val btnEdit: Button = itemView.findViewById(R.id.btn_edit)

    init {
      btnEdit.setOnClickListener { mFooterListener?.editPressed() }
    }
  }

  fun update(isEditing: Boolean) {
    mIsEditing = isEditing
    notifyDataSetChanged()
  }

  fun updateHeader(obj: Any?) {
    mHeaderObject = obj
    mHeaderAdapter?.notifyDataSetChanged()
    notifyDataSetChanged()
  }

  interface HeaderListener {
    fun dismissPressed()
    fun goToDailyLogPressed()
  }

  interface FooterListener {
    fun editPressed()
  }
}
