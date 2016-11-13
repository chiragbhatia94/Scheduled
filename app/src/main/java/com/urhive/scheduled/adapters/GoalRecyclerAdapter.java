package com.urhive.scheduled.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.urhive.scheduled.R;
import com.urhive.scheduled.helpers.ColorHelper;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.GoalReminder;
import com.urhive.scheduled.models.Icon;
import com.urhive.scheduled.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chirag Bhatia on 10-11-2016.
 */
public class GoalRecyclerAdapter extends RecyclerView.Adapter<GoalRecyclerAdapter.ItemViewHolder> {
    Context context;
    List<Reminder> remindersList;

    public GoalRecyclerAdapter(Context context, List<Reminder> remindersList) {
        this.context = context;
        this.remindersList = remindersList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_listview_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Reminder reminder = remindersList.get(position);

        holder.titleTV.setText(reminder.getTitle());
        holder.titleTV.setText(reminder.getContent());

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

        holder.timeTV.setText(reminder.getTime());

        Category category = Category.findById(Category.class, reminder.getCategoryId());
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

        List<GoalReminder> reminders = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            reminders.add(new GoalReminder(i));
        }

        ButtonAdapter adapter;
        /*RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.buttons.setLayoutManager(layoutManager);*/


        holder.buttons.setLayoutManager(new GridLayoutManager(context, 7));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        holder.buttons.addItemDecoration(itemDecoration);

        adapter = new ButtonAdapter(context, reminders);
        holder.buttons.setAdapter(adapter);
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
        ImageView iconIV, circleIV;
        RecyclerView buttons;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.reminderTitleTV);
            titleTV = (TextView) itemView.findViewById(R.id.reminderContentTV);
            timeTV = (TextView) itemView.findViewById(R.id.reminderDateTimeTV);
            repeatTypeTV = (TextView) itemView.findViewById(R.id.reminderRepeatTypeTV);
            shownTV = (TextView) itemView.findViewById(R.id.reminderShownToShowTV);

            circleIV = (ImageView) itemView.findViewById(R.id.circle);
            iconIV = (ImageView) itemView.findViewById(R.id.categoryIconIV);
            buttons = (RecyclerView) itemView.findViewById(R.id.weekdays_recycler_view);
        }
    }
}