package com.urhive.scheduled.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.urhive.scheduled.R;
import com.urhive.scheduled.helpers.ColorHelper;
import com.urhive.scheduled.models.AlarmReminders;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;
import com.urhive.scheduled.models.Reminder;

import java.util.List;

/**
 * Created by Chirag Bhatia on 06-11-2016.
 */
public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ItemViewHolder> {
    Context context;
    List<Reminder> remindersList;

    public ReminderRecyclerAdapter(Context context, List<Reminder> remindersList) {
        this.context = context;
        this.remindersList = remindersList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminderlistviewitem, parent, false);
        */
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_listview_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final Reminder reminder = remindersList.get(position);

        if (reminder.getActive() == 0) {
            holder.notiOnIV.setVisibility(View.GONE);
            holder.notiOffIV.setVisibility(View.VISIBLE);
        } else {
            holder.notiOnIV.setVisibility(View.VISIBLE);
            holder.notiOffIV.setVisibility(View.GONE);
        }

        holder.titleTV.setText(reminder.getTitle());
        holder.contentTV.setText(reminder.getContent());

        switch (remindersList.get(position).getRepeatType()) {
            case 0:
                holder.repeatTypeTV.setText(R.string.do_not_repeat);
                break;
            case 1:
                holder.repeatTypeTV.setText(R.string.every_day);
                break;
            case 2:
                holder.repeatTypeTV.setText(R.string.every_week);
                break;
            case 3:
                holder.repeatTypeTV.setText(R.string.every_month);
                break;
            case 4:
                holder.repeatTypeTV.setText(R.string.every_year);
                break;
            case 5:
                holder.repeatTypeTV.setText(Reminder.getActiveDays(reminder.getId()));
                break;
            case 6:
                holder.repeatTypeTV.setText(R.string.revision_preset);
                break;
            case 7:
                holder.repeatTypeTV.setText(R.string.alternate_days);
                break;
            case 8:
                holder.repeatTypeTV.setText(R.string.mwf_tts_alternate_week);
                break;
            case 9:
                holder.repeatTypeTV.setText(R.string.custom);
                break;
        }

        //holder.timeTV.setText(reminder.getDate() + " " + reminder.getTime());
        holder.timeTV.setText(reminder.getTime());

        final Category category = Category.findById(Category.class, reminder.getCategoryId());
        Icon icon = Icon.findById(Icon.class, category.getIconId());

        holder.iconIV.setImageResource(context.getResources().getIdentifier(icon.getName(), "drawable", context.getPackageName()));
        holder.circleIV.setColorFilter(ColorHelper.COLOR_PALLETE[category.getColor()]);
        int x = reminder.getNoShown();
        int y = reminder.getNoToShow();
        if (y == -1)
            holder.shownTV.setText("Shown " + x + " times");
        else if (x != y)
            holder.shownTV.setText("Shown: " + x + "/" + y);
        else
            holder.shownTV.setText(R.string.completed);

        View.OnClickListener toggleActiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                if (reminder.getActive() == Reminder.ACTIVE) {
                    reminder.setActive(Reminder.INACTIVE);
                    holder.notiOffIV.setVisibility(View.VISIBLE);
                } else {
                    reminder.setActive(Reminder.ACTIVE);
                    holder.notiOnIV.setVisibility(View.VISIBLE);
                }
                reminder.save();
                Reminder.resetAlarms();
            }
        };

        holder.notiOffIV.setOnClickListener(toggleActiveListener);
        holder.notiOnIV.setOnClickListener(toggleActiveListener);

        holder.popupmenuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(context, view);
                switch (reminder.getStatus()) {
                    case Reminder.STATUS_NORMAL:
                        popupMenu.getMenuInflater().inflate(R.menu.reminder_normal_item_menu, popupMenu.getMenu());
                        break;
                    case Reminder.STATUS_ARCHIEVED:
                        popupMenu.getMenuInflater().inflate(R.menu.reminder_archive_item_menu, popupMenu.getMenu());
                        break;
                    case Reminder.STATUS_DELETED:
                        popupMenu.getMenuInflater().inflate(R.menu.reminder_delete_item_menu, popupMenu.getMenu());
                        break;
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                Toast.makeText(context, "To be developed", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_archive:
                                reminder.setStatus(Reminder.STATUS_ARCHIEVED);
                                reminder.save();
                                remindersList.remove(position);
                                notifyDataSetChanged();
                                break;
                            case R.id.action_delete:
                                reminder.setStatus(Reminder.STATUS_DELETED);
                                reminder.save();
                                remindersList.remove(position);
                                notifyDataSetChanged();
                                Reminder.resetAlarms();
                                break;
                            case R.id.action_restore:
                                reminder.setStatus(Reminder.STATUS_NORMAL);
                                reminder.save();
                                remindersList.remove(position);
                                notifyDataSetChanged();
                                Reminder.resetAlarms();
                                break;
                            case R.id.action_unarchive:
                                reminder.setStatus(Reminder.STATUS_NORMAL);
                                reminder.save();
                                remindersList.remove(position);
                                notifyDataSetChanged();
                                break;
                            case R.id.action_delete_forever:
                                List<AlarmReminders> alarms = AlarmReminders.find(AlarmReminders.class, "reminder_id = ?", String.valueOf(reminder.getId()));
                                for (AlarmReminders alarm : alarms) {
                                    alarm.delete();
                                }
                                reminder.delete();
                                remindersList.remove(position);
                                notifyDataSetChanged();
                                Reminder.resetAlarms();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return remindersList.size();
    }

    public void notifyListChanged(List<Reminder> reminderList) {
        this.remindersList = reminderList;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, contentTV, timeTV, repeatTypeTV, shownTV;
        ImageView notiOffIV, notiOnIV, iconIV, circleIV, popupmenuIV;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.reminderTitleTV);
            contentTV = (TextView) itemView.findViewById(R.id.reminderContentTV);
            timeTV = (TextView) itemView.findViewById(R.id.reminderDateTimeTV);
            repeatTypeTV = (TextView) itemView.findViewById(R.id.reminderRepeatTypeTV);
            shownTV = (TextView) itemView.findViewById(R.id.reminderShownToShowTV);

            notiOffIV = (ImageView) itemView.findViewById(R.id.reminderNotiOFFIV);
            notiOnIV = (ImageView) itemView.findViewById(R.id.reminderNotiONIV);
            circleIV = (ImageView) itemView.findViewById(R.id.circle);
            iconIV = (ImageView) itemView.findViewById(R.id.categoryIconIV);
            popupmenuIV = (ImageView) itemView.findViewById(R.id.popupmenuIV);
        }
    }
}
