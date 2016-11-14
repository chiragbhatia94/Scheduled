package com.urhive.scheduled.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.urhive.scheduled.R;
import com.urhive.scheduled.fragments.CategoryFragment;
import com.urhive.scheduled.models.Icon;

import java.util.List;

/**
 * Created by Chirag Bhatia on 05-06-2016.
 */
public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder> {

    private int mRowLayout;
    private Context mContext;
    private List<Icon> mIconList;

    public IconsAdapter(Context context, int rowLayout, List<Icon> iconList) {
        mContext = context;
        mRowLayout = rowLayout;
        mIconList = iconList;
    }

    @Override
    public int getItemCount() {
        return mIconList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final String iconName = mIconList.get(position).getName();
        final int iconResId = mContext.getResources().getIdentifier(iconName, "drawable",
                mContext.getPackageName());
        viewHolder.mImageView.setImageResource(iconResId);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryFragment.imageSelected();
                CategoryFragment.image.setImageResource(iconResId);

                CategoryFragment.category.setIconId(Integer.parseInt(String.valueOf(mIconList.get
                        (position).getId())));
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        private View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.icon);
        }
    }
}
