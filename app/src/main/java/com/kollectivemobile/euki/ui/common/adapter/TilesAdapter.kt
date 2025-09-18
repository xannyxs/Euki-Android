package com.kollectivemobile.euki.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.model.TileItem
import com.kollectivemobile.euki.ui.common.holder.TileHolder
import com.kollectivemobile.euki.utils.Utils
import com.kollectivemobile.euki.utils.advrecyclerview.draggable.DraggableItemAdapter
import com.kollectivemobile.euki.utils.advrecyclerview.draggable.ItemDraggableRange

class TilesAdapter(private val mTileListener: TileHolder.TileListener?) :
        RecyclerView.Adapter<TileHolder>(), DraggableItemAdapter<TileHolder> {

  private val mTileItems: MutableList<TileItem> = ArrayList()
  private var mIsEditing: Boolean = false

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.layout_tile, parent, false)
    return TileHolder(view, mTileListener)
  }

  override fun onBindViewHolder(tileHolder: TileHolder, position: Int) {
    val item = mTileItems[position]

    tileHolder.ibAction.visibility = if (mIsEditing) View.VISIBLE else View.GONE
    tileHolder.ibAction.setImageResource(
            if (item.used) R.drawable.ic_tile_remove else R.drawable.ic_tile_add
    )
    tileHolder.ivIcon.setImageResource(Utils.getImageId(item.imageIcon))

    if (!item.title.isNullOrEmpty()) {
      tileHolder.tvTitle.text = item.title
    } else {
      tileHolder.tvTitle.text = Utils.getLocalized(item.id)
    }

    val params = tileHolder.cvMain.layoutParams as RelativeLayout.LayoutParams
    val margin = Utils.dpFromInt(6)
    params.setMargins(margin, margin, margin, margin)
    tileHolder.cvMain.layoutParams = params
  }

  override fun getItemId(position: Int): Long = mTileItems[position].itemId

  override fun getItemViewType(position: Int): Int = 0

  override fun getItemCount(): Int = mTileItems.size

  override fun onCheckCanStartDrag(holder: TileHolder, position: Int, x: Int, y: Int): Boolean {
    return mTileItems[position].used
  }

  override fun onGetItemDraggableRange(holder: TileHolder, position: Int): ItemDraggableRange? {
    return null
  }

  override fun onMoveItem(fromPosition: Int, toPosition: Int) {
    if (fromPosition == toPosition) {
      return
    }

    val tileItem = mTileItems.removeAt(fromPosition)
    mTileItems.add(toPosition, tileItem)
    mTileListener?.orderChanged(mTileItems)
  }

  override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
    return mTileItems[dropPosition].used
  }

  override fun onItemDragStarted(position: Int) {
    mTileListener?.editStarted()
    notifyItemRangeChanged(0, itemCount)
  }

  override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
    notifyDataSetChanged()
  }

  fun update(tileItems: List<TileItem>, isEditing: Boolean) {
    mIsEditing = isEditing
    mTileItems.clear()
    mTileItems.addAll(tileItems)
    notifyDataSetChanged()
  }
}
