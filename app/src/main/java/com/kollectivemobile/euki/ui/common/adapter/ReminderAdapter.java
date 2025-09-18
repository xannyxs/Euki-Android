package com.kollectivemobile.euki.ui.common.adapter;

import static com.kollectivemobile.euki.App.getContext;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.kollectivemobile.euki.R;
import com.kollectivemobile.euki.model.database.entity.ReminderItem;
import com.kollectivemobile.euki.ui.common.listeners.RemindersListener;
import com.kollectivemobile.euki.utils.DateUtils;
import com.kollectivemobile.euki.utils.Utils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter implements RemindersListener {
  public static final Integer VIEW_TYPE_NEW = 0;
  public static final Integer VIEW_TYPE_EXISTENT = 1;
  public static final Integer VIEW_TYPE_SCHEDULED_HEADER = 2;
  public static final Integer VIEW_TYPE_SCHEDULED_ITEM = 3;

  private Context mContext;
  private RemindersDataListener mListener;
  private List<ReminderItem> mItems;

  private ReminderItem mCurrentReminderItem;
  private Integer mCurrentReminderIndex;

  public ReminderAdapter(Context context, RemindersDataListener listener) {
    mItems = new ArrayList<>();
    mContext = context;
    mListener = listener;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    if (viewType == VIEW_TYPE_NEW) {
      View view = inflater.inflate(R.layout.layout_reminders_add, parent, false);
      return new NewHolder(view, this);
    }
    if (viewType == VIEW_TYPE_EXISTENT) {
      View view = inflater.inflate(R.layout.layout_reminders_existent, parent, false);
      return new ExistentHolder(view, this);
    }
    if (viewType == VIEW_TYPE_SCHEDULED_HEADER) {
      View view = inflater.inflate(R.layout.layout_reminders_scheduled_header, parent, false);
      return new HeaderHolder(view);
    }
    if (viewType == VIEW_TYPE_SCHEDULED_ITEM) {
      View view = inflater.inflate(R.layout.layout_reminders_scheduled_item, parent, false);
      return new ScheduledHolder(view, this);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ExistentHolder && mCurrentReminderItem != null) {
      ExistentHolder existentHolder = (ExistentHolder) holder;
      existentHolder.etTitle.setText(mCurrentReminderItem.getTitle());
      existentHolder.etText.setText(mCurrentReminderItem.getText());

      if (mCurrentReminderItem.getTitle() != null) {
        existentHolder.etTitle.setSelection(existentHolder.etTitle.getText().length());
      }
      if (mCurrentReminderItem.getText() != null) {
        existentHolder.etText.setSelection(existentHolder.etText.getText().length());
      }

      existentHolder.etTitle.addTextChangedListener(
          new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
              mCurrentReminderItem.setTitle(editable.toString());
            }
          });

      existentHolder.etText.addTextChangedListener(
          new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
              mCurrentReminderItem.setText(editable.toString());
            }
          });

      if (mCurrentReminderItem.getDate() == null) {
        existentHolder.tvDayTime.setText(mContext.getString(R.string.day_time));
      } else {
        existentHolder.tvDayTime.setText(
            DateUtils.toString(mCurrentReminderItem.getDate(), DateUtils.eeeMMMdyyyyhmma));
      }
      if (mCurrentReminderItem.getRepeatDays() == null) {
        existentHolder.tvRepeat.setTextColor(
            ContextCompat.getColor(getContext(), R.color.light_gray));
        existentHolder.tvRepeat.setText(mContext.getString(R.string.repeat));
      } else {
        existentHolder.tvRepeat.setTextColor(
            ContextCompat.getColor(getContext(), R.color.euki_main));
        if (mCurrentReminderItem.getRepeatDays() == 0) {
          existentHolder.tvRepeat.setText(mContext.getString(R.string.none));
        } else {
          existentHolder.tvRepeat.setText(
              String.format(
                  Utils.getLocalized("repeat_format"), mCurrentReminderItem.getRepeatDays()));
        }
      }
      existentHolder.llNew.setVisibility(
          mCurrentReminderItem.getId() == 0 ? View.VISIBLE : View.GONE);
      existentHolder.btnAdd.setText(
          mContext.getString(mCurrentReminderItem.getId() == 0 ? R.string.add : R.string.save));
    }
    if (holder instanceof ScheduledHolder) {
      ScheduledHolder scheduledHolder = (ScheduledHolder) holder;
      scheduledHolder.tvTitle.setText(mItems.get(position - 2).getTitle());
    }
  }

  @Override
  public int getItemCount() {
    return mItems.size() + (mItems.size() > 0 ? 2 : 1);
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      if (mCurrentReminderItem != null && mCurrentReminderItem.getId() == 0) {
        return VIEW_TYPE_EXISTENT;
      }
      return VIEW_TYPE_NEW;
    }
    if (position == 1) {
      return VIEW_TYPE_SCHEDULED_HEADER;
    }
    if (mCurrentReminderIndex != null && mCurrentReminderIndex == position - 2) {
      return VIEW_TYPE_EXISTENT;
    }
    return VIEW_TYPE_SCHEDULED_ITEM;
  }

  @Override
  public void newSelected() {
    mCurrentReminderItem = new ReminderItem();
    mCurrentReminderIndex = null;
    notifyDataSetChanged();
  }

  @Override
  public void dayTimeSelected() {
    if (mCurrentReminderItem != null) {
      mListener.dayTimeSelected(mCurrentReminderItem.getDate());
    }
  }

  @Override
  public void repeatSelected() {
    mListener.repeatSelected();
  }

  @Override
  public void reminderSelected(int position) {
    mCurrentReminderItem = mItems.get(position).copy();
    mCurrentReminderIndex = position;
    notifyDataSetChanged();
  }

  @Override
  public void saveSelected(String title, String text) {
    ReminderItem editedItem = null;
    if (mCurrentReminderIndex != null) {
      editedItem = mItems.get(mCurrentReminderIndex);
    } else {
      editedItem = mCurrentReminderItem;
    }

    if (editedItem == null) {
      return;
    }

    editedItem.setTitle(title);
    editedItem.setText(text);

    if (mCurrentReminderItem != null) {
      editedItem.setDate(mCurrentReminderItem.getDate());
      editedItem.setRepeatDays(mCurrentReminderItem.getRepeatDays());
    }

    if (mListener != null) {
      mListener.saveReminder(editedItem);
    }
  }

  @Override
  public void cancelSelected() {
    clear();
  }

  public class NewHolder extends RecyclerView.ViewHolder {
    private RemindersListener mListener;
    public View llMain;

    public NewHolder(@NonNull View itemView, RemindersListener listener) {
      super(itemView);
      mListener = listener;
      llMain = itemView.findViewById(R.id.ll_main);

      // Set click listener manually
      llMain.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mListener != null) {
                mListener.newSelected();
              }
            }
          });
    }
  }

  public class HeaderHolder extends RecyclerView.ViewHolder {
    public HeaderHolder(@NonNull View itemView) {
      super(itemView);
    }
  }

  public class ScheduledHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle;
    private RemindersListener mListener;
    public View llMain;

    public ScheduledHolder(@NonNull View itemView, RemindersListener listener) {
      super(itemView);
      mListener = listener;
      llMain = itemView.findViewById(R.id.ll_main);
      tvTitle = itemView.findViewById(R.id.tv_title);

      // Set click listener manually
      llMain.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mListener != null) {
                mListener.reminderSelected(getLayoutPosition() - 2);
              }
            }
          });
    }
  }

  public class ExistentHolder extends RecyclerView.ViewHolder {
    public LinearLayout llNew;
    public EditText etTitle;
    public EditText etText;
    public TextView tvDayTime;
    public TextView tvRepeat;
    public Button btnAdd;
    private RemindersListener mListener;

    public ExistentHolder(@NonNull View itemView, RemindersListener listener) {
      super(itemView);
      mListener = listener;
      llNew = itemView.findViewById(R.id.ll_new);
      etTitle = itemView.findViewById(R.id.et_title);
      etText = itemView.findViewById(R.id.et_text);
      tvDayTime = itemView.findViewById(R.id.tv_day_time);
      tvRepeat = itemView.findViewById(R.id.tv_repeat);
      btnAdd = itemView.findViewById(R.id.btn_add);

      // Set click listeners manually
      llNew.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mListener != null) {
                mListener.newSelected();
              }
            }
          });

      tvDayTime.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mListener != null) {
                mListener.dayTimeSelected();
              }
            }
          });

      tvRepeat.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mListener != null) {
                mListener.repeatSelected();
              }
            }
          });

      itemView
          .findViewById(R.id.btn_cancel)
          .setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (mListener != null) {
                    mListener.cancelSelected();
                  }
                }
              });

      btnAdd.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mListener != null) {
                mListener.saveSelected(etTitle.getText().toString(), etText.getText().toString());
              }
            }
          });
    }
  }

  public void update(List<ReminderItem> reminderItems) {
    if (reminderItems != null) {
      mItems.clear();
      mItems.addAll(reminderItems);
      clear();
    }
  }

  public void clear() {
    mCurrentReminderItem = null;
    mCurrentReminderIndex = null;
    notifyDataSetChanged();
  }

  public void selectDate(Date date) {
    mCurrentReminderItem.setDate(date);
    notifyDataSetChanged();
  }

  public void selectRepeat(Integer repeat) {
    mCurrentReminderItem.setRepeatDays(repeat);
    notifyDataSetChanged();
  }

  public interface RemindersDataListener {
    void saveReminder(ReminderItem reminderItem);

    void dayTimeSelected(Date date);

    void repeatSelected();
  }
}
