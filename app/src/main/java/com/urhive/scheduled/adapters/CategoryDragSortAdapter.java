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
import com.urhive.scheduled.activities.EditLabelsActivity;
import com.urhive.scheduled.fragments.CategoryFragment;
import com.urhive.scheduled.helpers.ColorHelper;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;

import java.util.List;

/**
 * Created by Chirag Bhatia on 05-06-2016.
 */
public class CategoryDragSortAdapter extends BaseAdapter {

    Context context;
    List<Category> categoryList;
    List<Icon> iconList;
    View view;
    private List selectedPositions;

    public CategoryDragSortAdapter(Context context, List<Category> categoryList, List<Icon>
            iconList) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        final ViewHolder viewHolder;
        boolean selected = selectedPositions.contains(position);

        if (cell == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            cell = layoutInflater.inflate(R.layout.edit_category_listview_item, null, false);
            viewHolder = new ViewHolder(cell);
            cell.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cell.getTag();
        }
        if (selected) {
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.border_list_item);
        } else {
            viewHolder.relativeLayout.setBackgroundResource(android.R.color.transparent);
        }

        viewHolder.nameTV.setText(categoryList.get(position).getName());
        viewHolder.iconIV.setImageResource(context.getResources().getIdentifier(iconList.get
                (categoryList.get(position).getIconId() - 1).getName(), "drawable", context
                .getPackageName()));
        viewHolder.circleIV.setColorFilter(ColorHelper.COLOR_PALLETE[categoryList.get(position)
                .getColor()]);

        viewHolder.editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryList.get(position).getId() == 1) {
                    Toast.makeText(context, "This cannot be edited!", Toast.LENGTH_SHORT).show();
                } else {
                    CategoryFragment addCategoryFragment = CategoryFragment.editCategory
                            (categoryList.get(position).getId());
                    addCategoryFragment.show(((EditLabelsActivity) context)
                            .getSupportFragmentManager(), "EditCategory");
                }
            }
        });
        return cell;
    }

    public void setSelectedPositions(List selectedPositions) {
        this.selectedPositions = selectedPositions;
    }

    private class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView iconIV, editIV, circleIV;
        TextView nameTV;

        ViewHolder(View v) {
            relativeLayout = (RelativeLayout) v.findViewById(R.id.list_item_relative_layout);
            iconIV = (ImageView) v.findViewById(R.id.categoryIconIV);
            editIV = (ImageView) v.findViewById(R.id.editCategoryIcon);
            nameTV = (TextView) v.findViewById(R.id.categoryNameTV);
            circleIV = (ImageView) v.findViewById(R.id.circle);

            view = v;
        }
    }
}
