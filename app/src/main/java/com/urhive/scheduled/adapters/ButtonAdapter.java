package com.urhive.scheduled.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.urhive.scheduled.R;
import com.urhive.scheduled.models.GoalReminder;

import java.util.List;

/**
 * Created by Chirag Bhatia on 06-11-2016.
 */
public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
    Context context;
    List<GoalReminder> list;
    float x;

    public ButtonAdapter(Context context, List<GoalReminder> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.buttonlistviewitem, parent, false);
        x = parent.getWidth() / getItemCount();
        /*ButtonViewHolder holder =new ButtonViewHolder(v);;
        holder.mViewHolder.setMinimumWidth(Math.round(x));
        return holder;*/

        return new ButtonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ButtonViewHolder holder, final int position) {
        // holder.noTV.setText("C");

        holder.noTV.setText(list.get(position).getNo() + "");
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is awesome! " + list.get(position).getNo(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mViewHolder.setMinimumWidth(Math.round(x));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ButtonViewHolder extends RecyclerView.ViewHolder {
        TextView noTV;
        ImageView button;
        View mViewHolder;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            mViewHolder = itemView;
            button = (ImageView) itemView.findViewById(R.id.circle);
            noTV = (TextView) itemView.findViewById(R.id.noTV);
        }
    }
}
