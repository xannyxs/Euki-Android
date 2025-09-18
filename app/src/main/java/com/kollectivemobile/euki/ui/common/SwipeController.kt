package com.kollectivemobile.euki.ui.common

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kollectivemobile.euki.App
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.ui.common.adapter.AppointmentAdapter
import com.kollectivemobile.euki.ui.common.adapter.CycleSummaryAdapter
import com.kollectivemobile.euki.ui.common.adapter.ReminderAdapter
import com.kollectivemobile.euki.ui.common.holder.DailyLogAppointmentExistentHolder

enum class ButtonsState {
  GONE,
  LEFT_VISIBLE,
  RIGHT_VISIBLE
}

class SwipeController(private val buttonsActions: SwipeControllerActions?) :
        ItemTouchHelper.Callback() {

  private var swipeBack = false
  private var buttonShowedState = ButtonsState.GONE
  private var buttonInstance: RectF? = null
  private var currentItemViewHolder: RecyclerView.ViewHolder? = null

  companion object {
    private val buttonWidth = App.getContext()?.resources?.getDimension(R.dimen.dimen_x12_10) ?: 0f
  }

  override fun getMovementFlags(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder
  ): Int {
    return when (viewHolder) {
      is ReminderAdapter.ScheduledHolder,
      is AppointmentAdapter.ExistentHolder,
      is DailyLogAppointmentExistentHolder -> makeMovementFlags(0, ItemTouchHelper.LEFT)
      else -> {
        if (viewHolder.javaClass == CycleSummaryAdapter.ItemHolder::class.java) {
          makeMovementFlags(0, ItemTouchHelper.LEFT)
        } else {
          0
        }
      }
    }
  }

  override fun onMove(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          target: RecyclerView.ViewHolder
  ): Boolean = false

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    // Empty implementation
  }

  override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
    if (swipeBack) {
      swipeBack = buttonShowedState != ButtonsState.GONE
      return 0
    }
    return super.convertToAbsoluteDirection(flags, layoutDirection)
  }

  override fun onChildDraw(
          c: Canvas,
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          dX: Float,
          dY: Float,
          actionState: Int,
          isCurrentlyActive: Boolean
  ) {
    var adjustedDX = dX

    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      if (buttonShowedState != ButtonsState.GONE) {
        when (buttonShowedState) {
          ButtonsState.LEFT_VISIBLE -> adjustedDX = maxOf(dX, buttonWidth)
          ButtonsState.RIGHT_VISIBLE -> adjustedDX = minOf(dX, -buttonWidth)
          else -> {
            /* no-op */
          }
        }
        super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                adjustedDX,
                dY,
                actionState,
                isCurrentlyActive
        )
      } else {
        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      }
    }

    if (buttonShowedState == ButtonsState.GONE) {
      super.onChildDraw(c, recyclerView, viewHolder, adjustedDX, dY, actionState, isCurrentlyActive)
    }
    currentItemViewHolder = viewHolder
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setTouchListener(
          c: Canvas,
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          dX: Float,
          dY: Float,
          actionState: Int,
          isCurrentlyActive: Boolean
  ) {
    recyclerView.setOnTouchListener { _, event ->
      swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
      if (swipeBack) {
        buttonShowedState =
                when {
                  dX < -buttonWidth -> ButtonsState.RIGHT_VISIBLE
                  dX > buttonWidth -> ButtonsState.LEFT_VISIBLE
                  else -> buttonShowedState
                }

        if (buttonShowedState != ButtonsState.GONE) {
          setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
          setItemsClickable(recyclerView, false)
        }
      }
      false
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setTouchDownListener(
          c: Canvas,
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          dX: Float,
          dY: Float,
          actionState: Int,
          isCurrentlyActive: Boolean
  ) {
    recyclerView.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      }
      false
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setTouchUpListener(
          c: Canvas,
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          dX: Float,
          dY: Float,
          actionState: Int,
          isCurrentlyActive: Boolean
  ) {
    recyclerView.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_UP) {
        super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
        recyclerView.setOnTouchListener { _, _ -> false }
        setItemsClickable(recyclerView, true)
        swipeBack = false

        val buttonInstance = this.buttonInstance
        if (buttonsActions != null &&
                        buttonInstance != null &&
                        buttonInstance.contains(event.x, event.y)
        ) {

          when (buttonShowedState) {
            ButtonsState.LEFT_VISIBLE ->
                    buttonsActions.onLeftClicked(viewHolder.bindingAdapterPosition)
            ButtonsState.RIGHT_VISIBLE ->
                    buttonsActions.onRightClicked(viewHolder.bindingAdapterPosition)
            else -> {
              /* no-op */
            }
          }
        }
        buttonShowedState = ButtonsState.GONE
        currentItemViewHolder = null
      }
      false
    }
  }

  private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
    for (i in 0 until recyclerView.childCount) {
      recyclerView.getChildAt(i).isClickable = isClickable
    }
  }

  private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
    val buttonWidthWithoutPadding = buttonWidth
    val corners = 16f
    val context = App.getContext() ?: return

    val itemView = viewHolder.itemView
    val paint = Paint()

    val leftButton =
            RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.left + buttonWidthWithoutPadding,
                    itemView.bottom.toFloat()
            )
    paint.color = Color.BLUE
    c.drawRoundRect(leftButton, corners, corners, paint)
    drawText(context.getString(R.string.edit).uppercase(), c, leftButton, paint)

    val rightButton =
            RectF(
                    itemView.right - buttonWidthWithoutPadding,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
            )
    paint.color = ContextCompat.getColor(context, R.color.pale_lavender)
    c.drawRect(rightButton, paint)
    drawText(context.getString(R.string.delete).uppercase(), c, rightButton, paint)

    buttonInstance =
            when (buttonShowedState) {
              ButtonsState.LEFT_VISIBLE -> leftButton
              ButtonsState.RIGHT_VISIBLE -> rightButton
              else -> null
            }
  }

  private fun drawText(text: String, c: Canvas, button: RectF, paint: Paint) {
    val textSize = 60f
    val context = App.getContext() ?: return

    paint.color = ContextCompat.getColor(context, R.color.blueberry)
    paint.isAntiAlias = true
    paint.textSize = textSize

    val textWidth = paint.measureText(text)
    c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), paint)
  }

  fun onDraw(c: Canvas) {
    currentItemViewHolder?.let { viewHolder -> drawButtons(c, viewHolder) }
  }
}
