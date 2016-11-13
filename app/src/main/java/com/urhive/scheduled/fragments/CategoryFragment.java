package com.urhive.scheduled.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.spectrum.SpectrumDialog;
import com.urhive.scheduled.R;
import com.urhive.scheduled.activities.AddReminderActivity;
import com.urhive.scheduled.activities.EditLabelsActivity;
import com.urhive.scheduled.adapters.IconsAdapter;
import com.urhive.scheduled.adapters.ItemOffsetDecoration;
import com.urhive.scheduled.helpers.ColorHelper;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;

import java.util.List;

/**
 * Created by Chirag Bhatia on 03-06-2016.
 */
public class CategoryFragment extends AppCompatDialogFragment {
    public static int EDIT_MODE = 0;
    public static int ADD_MODE = 1;
    public static int IMMIDIATE_ADD_MODE = 2;
    public static Category category;
    public static ImageView circle, image;
    static AlertDialog iconSelectorDialog;
    private int mode;
    private TextView discardTV, saveTV, colorSelectTV, iconSelectTV;
    private EditText nameET;
    private int initIconId;

    public static CategoryFragment addImmidiateCategory(){
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt("mode", IMMIDIATE_ADD_MODE);

        categoryFragment.setArguments(args);
        return categoryFragment;
    }

    public static CategoryFragment editCategory(long id) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putLong("categoryId", id);
        args.putInt("mode", EDIT_MODE);

        categoryFragment.setArguments(args);
        return categoryFragment;
    }

    public static CategoryFragment addCategory() {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt("mode", ADD_MODE);

        categoryFragment.setArguments(args);
        return categoryFragment;
    }

    public static void imageSelected() {
        iconSelectorDialog.dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        nameET = (EditText) view.findViewById(R.id.categoryNameET);

        circle = (ImageView) view.findViewById(R.id.circle);
        image = (ImageView) view.findViewById(R.id.image);

        colorSelectTV = (TextView) view.findViewById(R.id.categoryColorTV);
        iconSelectTV = (TextView) view.findViewById(R.id.categoryIconTV);

        discardTV = (TextView) view.findViewById(R.id.categoryFragmentDiscardTV);
        saveTV = (TextView) view.findViewById(R.id.categoryFragmentSaveTV);

        Bundle args = getArguments();
        mode = (Integer) args.get("mode");

        System.out.println(mode+"");

        if (mode == ADD_MODE || mode == IMMIDIATE_ADD_MODE) {
            getDialog().setTitle("Add Category");

            category = new Category("", 12, 1, (int) Category.count(Category.class), 0);

        } else if (mode == EDIT_MODE) {
            getDialog().setTitle("Edit Category");

            long categoryId = (long) args.get("categoryId");
            category = Category.findById(Category.class, categoryId);
            initIconId = category.getIconId();
        }

        Icon ic = Icon.findById(Icon.class, category.getIconId());
        nameET.setText(category.getName());
        circle.setColorFilter(ColorHelper.COLOR_PALLETE[category.getColor()]);
        image.setImageResource(getResources().getIdentifier(ic.getName(), "drawable", getContext().getPackageName()));

        colorSelectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpectrumDialog.Builder builder = new SpectrumDialog.Builder(getContext())
                        .setTitle("Color Picker")
                        .setSelectedColor(ColorHelper.COLOR_PALLETE[category.getColor()])
                        .setColors(ColorHelper.COLOR_PALLETE);
                builder.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        circle.setColorFilter(color);
                        for (int i = 0; i < ColorHelper.COLOR_PALLETE.length; i++) {
                            if (ColorHelper.COLOR_PALLETE[i] == color) {
                                category.setColor(i);
                            }
                        }
                    }
                });
                builder.build().show(getFragmentManager(), "ColorDialog");
            }
        });

        iconSelectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = inflater.inflate(R.layout.view_dialog_recycler_view, null, false);

                RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.dialogRecyclerView);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_columns)));
                ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
                recyclerView.addItemDecoration(itemDecoration);

                List<Icon> list = Icon.listAll(Icon.class);

                recyclerView.setAdapter(new IconsAdapter(getContext(), R.layout.item_icon_grid, list));

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Icon")
                        .setView(dialogView);
                iconSelectorDialog = builder.show();
            }
        });

        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                if (name.isEmpty()) {
                    nameET.setError("Category Name Cannot Be Empty!");
                    return;
                }
                if (mode == ADD_MODE || mode == IMMIDIATE_ADD_MODE) {
                    List<Category> list = Category.find(Category.class, "name = ?", name);
                    if (list.size() > 0) {
                        nameET.setError("Category Name Already Registered!");
                    } else {
                        category.setName(name);
                        category.save();
                        Icon icon = Icon.findById(Icon.class, category.getIconId());
                        icon.setUseFrequency(icon.getUseFrequency() + 1);
                        icon.save();
                        if (mode==ADD_MODE){
                            EditLabelsActivity.notifyListView();
                        }
                        else {
                            AddReminderActivity.category = category;
                            AddReminderActivity.categoryTV.setText(category.getName());

                            AddReminderActivity.circle.setColorFilter(ColorHelper.COLOR_PALLETE[category.getColor()]);
                            AddReminderActivity.categoryIcon.setImageResource(getResources().getIdentifier(icon.getName(),"drawable",getContext().getPackageName()));
                        }
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else if (mode == EDIT_MODE) {
                    category.setName(name);
                    if (category.getIconId() != initIconId) {
                        Icon icon = Icon.findById(Icon.class, category.getIconId());
                        icon.setUseFrequency(icon.getUseFrequency() + 1);
                        icon.save();
                    }
                    category.save();
                    EditLabelsActivity.notifyListView();
                    Toast.makeText(getContext(), "Change Saved!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        discardTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Discarded!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return view;
    }
}
