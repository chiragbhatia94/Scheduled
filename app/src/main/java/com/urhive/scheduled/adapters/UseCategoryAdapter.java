package com.urhive.scheduled.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.urhive.scheduled.R;
import com.urhive.scheduled.activities.AddReminderActivity;
import com.urhive.scheduled.helpers.ColorHelper;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;

import java.util.List;

/**
 * Created by Chirag Bhatia on 09-06-2016.
 */
public class UseCategoryAdapter extends BaseAdapter {

    Context context;
    List<Category> categoryList;
    List<Icon> iconList;

    View view;

    public UseCategoryAdapter(Context context, List<Category> categoryList, List<Icon> iconList) {
        this.context = context;
        this.categoryList = categoryList;
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categoryList.get(position).getId();
    }

    private class ViewHolder {
        ImageView iconIV, circleIV;
        TextView nameTV;

        ViewHolder(View v) {
            iconIV = (ImageView) v.findViewById(R.id.categoryIconIV);
            nameTV = (TextView) v.findViewById(R.id.categoryNameTV);
            circleIV = (ImageView) v.findViewById(R.id.circle);

            view = v;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        final ViewHolder viewHolder;

        if (cell == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            cell = layoutInflater.inflate(R.layout.use_category_listview_item, null, false);
            viewHolder = new ViewHolder(cell);
            cell.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cell.getTag();
        }

        viewHolder.nameTV.setText(categoryList.get(position).getName());
        viewHolder.iconIV.setImageResource(context.getResources().getIdentifier(iconList.get(categoryList.get(position).getIconId() - 1).getName(), "drawable", context.getPackageName()));
        viewHolder.circleIV.setColorFilter(ColorHelper.COLOR_PALLETE[categoryList.get(position).getColor()]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category category = categoryList.get(position);

                Icon icon = Icon.findById(Icon.class, category.getIconId());
                icon.setUseFrequency(icon.getUseFrequency() + 1);
                icon.save();

                AddReminderActivity.category = category;
                AddReminderActivity.categoryTV.setText(category.getName());

                AddReminderActivity.circle.setColorFilter(ColorHelper.COLOR_PALLETE[category.getColor()]);
                AddReminderActivity.categoryIcon.setImageResource(context.getResources().getIdentifier(icon.getName(),"drawable",context.getPackageName()));

                AddReminderActivity.categoryAlertBox.dismiss();
            }
        });

        return cell;
    }
}
