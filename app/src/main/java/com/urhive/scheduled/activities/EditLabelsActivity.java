package com.urhive.scheduled.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.urhive.scheduled.R;
import com.urhive.scheduled.adapters.CategoryDragSortAdapter;
import com.urhive.scheduled.fragments.CategoryFragment;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;

import java.util.LinkedList;
import java.util.List;

public class EditLabelsActivity extends AppCompatActivity implements
        DragSortListView.DropListener {

    static List<Category> categoryList;
    static CategoryDragSortAdapter adp;
    int NORMAL_MODE = 0;
    int SELECTION_MODE = 1;
    int REORDER_MODE = 2;
    int actionMode = NORMAL_MODE;
    DragSortListView listView;
    List<Icon> iconList;
    private List<Integer> selectedPositions;

    public static void notifyListView() {
        categoryList.clear();
        categoryList.addAll(Category.find(Category.class, null, null, null, "position", null));
        adp.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_labels);
        getSupportActionBar().setTitle("Edit Categories");

        selectedPositions = new LinkedList<>();

        categoryList = Category.find(Category.class, null, null, null, "position", null);
        iconList = Icon.listAll(Icon.class);

        listView = (DragSortListView) findViewById(R.id.listView);

        adp = new CategoryDragSortAdapter(EditLabelsActivity.this, categoryList, iconList);
        adp.setSelectedPositions(selectedPositions);

        DragSortController dragSortController = new CategoryDragSortController();

        listView.setAdapter(adp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode == SELECTION_MODE) {
                    selectItem(position);
                    if (selectedPositions.isEmpty()) {
                        actionMode = NORMAL_MODE;
                        EditLabelsActivity.this.invalidateOptionsMenu();
                    }
                } else {
                    Toast.makeText(EditLabelsActivity.this, "Long press to select!", Toast
                            .LENGTH_SHORT).show();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {
                if (actionMode == NORMAL_MODE) {
                    actionMode = SELECTION_MODE;
                    EditLabelsActivity.this.invalidateOptionsMenu();
                    selectItem(position);
                }
                return true;
            }
        });
        listView.setDropListener(this);
        listView.setFloatViewManager(dragSortController);
        listView.setDragEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (actionMode == NORMAL_MODE) {
            getMenuInflater().inflate(R.menu.edit_labels, menu);
        } else if (actionMode == SELECTION_MODE) {
            getMenuInflater().inflate(R.menu.delete_labels, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add:
                CategoryFragment addCategoryFragment = CategoryFragment.addCategory();
                addCategoryFragment.show(getSupportFragmentManager(), "AddCategory");
                return true;
            case R.id.action_reorder:
                listView.setDragEnabled(true);
                Toast.makeText(EditLabelsActivity.this, "Long press & drag to reorder!", Toast
                        .LENGTH_SHORT).show();
                actionMode = REORDER_MODE;
                this.invalidateOptionsMenu();
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(EditLabelsActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Deleted categories will not be recovered!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(EditLabelsActivity.this, "delete these items\n"
                                // + selectedPositions.toString(), Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < categoryList.size(); i++) {
                                    if (selectedPositions.contains(categoryList.get(i)
                                            .getPosition())) {
                                        // TODO
                                        // all those reminders & goals with this category to
                                        // go inside category id 1 uncategorized

                                        Category.executeQuery("update reminder set category_id = " +
                                                "1 where category_id = " + categoryList.get(i)
                                                .getId() + ";");

                                        Category.delete(categoryList.get(i));
                                        Toast.makeText(EditLabelsActivity.this, "TODO work left",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                selectedPositions.clear();
                                actionMode = NORMAL_MODE;
                                invalidateOptionsMenu();
                                notifyListView();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                Toast.makeText(EditLabelsActivity.this, "Yet to be developed!", Toast
                        .LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (actionMode == NORMAL_MODE) {
            Intent intent = new Intent(EditLabelsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            listView.setDragEnabled(false);
            actionMode = NORMAL_MODE;
            EditLabelsActivity.this.invalidateOptionsMenu();
            selectedPositions.clear();
            adp.notifyDataSetChanged();
        }
    }

    public void selectItem(int position) {
        if (!selectedPositions.contains(position))
            if (categoryList.get(position).getId() != 1)
                selectedPositions.add(position);
            else
                return;
        else {
            selectedPositions.remove(selectedPositions.indexOf(position));
        }
        adp.notifyDataSetChanged();
    }

    @Override
    public void drop(int from, int to) {
        if (from == to)
            return;
        reorderCategories(from, to);
        adp.notifyDataSetChanged();
    }

    public void reorderCategories(int from, int to) {
        Category fromCategory = categoryList.get(from);
        // Category toCategory = categoryList.get(to);

        categoryList.remove(from);
        categoryList.add(to, fromCategory);

        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setPosition(i);
        }

        Category.saveInTx(categoryList);
    }

    private class CategoryDragSortController extends DragSortController {
        public CategoryDragSortController() {
            super(EditLabelsActivity.this.listView);
            setRemoveEnabled(false);
        }

        @Override
        public View onCreateFloatView(int position) {
            return adp.getView(position, null, null);
        }

        @Override
        public void onDestroyFloatView(View floatView) {
        }
    }
}
