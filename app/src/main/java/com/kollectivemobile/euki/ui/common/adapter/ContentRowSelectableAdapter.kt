package com.kollectivemobile.euki.ui.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.model.ContentItem
import java.lang.ref.WeakReference

class ContentRowSelectableAdapter(
        context: Context,
        items: List<ContentItem>,
        private val mListener: ContentRowSelectableListener?
) : RecyclerView.Adapter<ContentRowSelectableAdapter.RowHolder>() {

  private val mContentItems: MutableList<ContentItem> = ArrayList()
  private val mContext: WeakReference<Context> = WeakReference(context)

  init {
    mContentItems.addAll(items)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.layout_content_row_selectable, parent, false)
    return RowHolder(view, mListener)
  }

  override fun onBindViewHolder(holder: RowHolder, position: Int) {
    val contentItem = mContentItems[position]

    holder.tvTitle.text = contentItem.localizedTitle
    holder.vSeparatorTop.visibility = View.VISIBLE
    holder.vSeparatorBottom.visibility =
            if (position == mContentItems.size - 1) View.VISIBLE else View.GONE
  }

  override fun getItemCount(): Int = mContentItems.size

  class RowHolder(itemView: View, private val mListener: ContentRowSelectableListener?) :
          RecyclerView.ViewHolder(itemView) {

    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val vSeparatorTop: View = itemView.findViewById(R.id.v_separator_top)
    val vSeparatorBottom: View = itemView.findViewById(R.id.v_separator_bottom)

    init {
      itemView.findViewById<View>(R.id.rl_main).setOnClickListener { onClick() }
    }

    private fun onClick() {
      mListener?.rowSelected(bindingAdapterPosition - 1)
    }
  }

  interface ContentRowSelectableListener {
    fun rowSelected(position: Int)
  }
}
