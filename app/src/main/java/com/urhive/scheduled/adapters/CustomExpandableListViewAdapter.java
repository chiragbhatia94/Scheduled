package com.urhive.scheduled.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.urhive.scheduled.R;
import com.urhive.scheduled.activities.AddReminderActivity;
import com.urhive.scheduled.models.AlarmReminders;
import com.urhive.scheduled.utils.DateTimeUtil;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.List;

/**
 * Created by Chirag Bhatia on 11-07-2016.
 */
public class CustomExpandableListViewAdapter extends BaseAdapter implements TimePickerDialog
        .OnTimeSetListener {
    Context context;
    List<AlarmReminders> list;
    FragmentManager fragmentManager;

    String date, time;
    int mHour, mMinute;
    int p = 0;

    public CustomExpandableListViewAdapter(Context context, List<AlarmReminders> list,
                                           FragmentManager fm) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fm;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return list.get(position).getReminderId();
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item
     *                    whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not
     *                    possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that
     *                    this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        final ViewHolder holder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (cell == null) {
            cell = layoutInflater.inflate(R.layout.customreminderlistitem, null);
            holder = new ViewHolder(cell);
            cell.setTag(holder);
        } else {
            holder = (ViewHolder) cell.getTag();
        }

        if (!AddReminderActivity.customizing) {
            holder.removeFab.setVisibility(View.GONE);
        } else {
            holder.removeFab.setVisibility(View.VISIBLE);
            holder.removeFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    AlarmReminders.sortCustomReminderListByDateTimeAndArrangeByNumber(list);
                    notifyDataSetChanged();
                }
            });

            holder.removeFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "yet to be implemented", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    if (list.size() == 0) {
                        AddReminderActivity.resetToDoNotRepeat();
                    }
                    AlarmReminders.sortCustomReminderListByDateTimeAndArrangeByNumber(list);
                    notifyDataSetChanged();
                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p = position;
                    date = list.get(position).getDate();
                    time = list.get(position).getTime();

                    String t[] = time.split(":");
                    mHour = Integer.parseInt(t[0]);
                    mMinute = Integer.parseInt(t[1]);

                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                            CustomExpandableListViewAdapter.this,
                            mHour,
                            mMinute,
                            false
                    );
                    tpd.setThemeDark(false);
                    tpd.show(fragmentManager, "Timepickerdialog");
                }
            });
        }

        holder.heading.setText("Reminder - " + list.get(position).getNumber());
        holder.dateTime.setText(list.get(position).getDate() + " " + list.get(position).getTime());
        return cell;
    }

    /**
     * @param view      The view associated with this listener.
     * @param hourOfDay The hour that was set.
     * @param minute    The minute that was set.
     */
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;

        time = DateTimeUtil.getTime(mHour, mMinute);

        // changing the date and time for this position
        list.get(p).setDate(date);
        list.get(p).setTime(time);

        AlarmReminders.sortCustomReminderListByDateTimeAndArrangeByNumber(list);

        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView heading;
        TextView dateTime;
        ImageView removeFab;
        View view;

        ViewHolder(View v) {
            heading = (TextView) v.findViewById(R.id.custom_reminder_heading);
            dateTime = (TextView) v.findViewById(R.id.custom_reminder_datetimetext);
            removeFab = (ImageView) v.findViewById(R.id.removeFab);
            view = v;
        }
    }
}
