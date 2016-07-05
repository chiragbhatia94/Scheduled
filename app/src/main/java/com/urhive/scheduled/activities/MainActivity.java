package com.urhive.scheduled.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.urhive.scheduled.R;
import com.urhive.scheduled.adapters.ReminderGoalViewPagerAdapter;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    ReminderGoalViewPagerAdapter viewPagerAdapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerAdapter = new ReminderGoalViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);

        PreferenceManager.setDefaultValues(this, R.xml.pref, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean firstRun = prefs.getBoolean("pref_first_run", true);

        // things to do no first run
        if (firstRun) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("pref_first_run", false);
            editor.apply();

            // run the app intro activity here
            String icons[] = MainActivity.this.getResources().getStringArray(R.array.icons_string_array);
            for (int i = 0; i < icons.length; i++) {
                Icon icon = new Icon(icons[i], 0);
                icon.save();
                // System.out.println("Icon saved! " + icon.getId());
            }

            List<Icon> iconsU = Icon.find(Icon.class,"name = ?","ic_label_white_24dp");
            Category uncategorized = new Category("Uncategorized",12,Integer.parseInt(String.valueOf(iconsU.get(0).getId())),0,0);
            uncategorized.save();
        }

        // navigation drawer
        List<IDrawerItem> drawerItems = new ArrayList<>();
        // add labels later here via a for loop with their id as identifier and total items in them as badges
        List<Category> categories = Category.find(Category.class, null, null, null, "position", null);
        Log.i("All Categories", "onCreate: "+categories.toString());

        for (Category category : categories) {
            Icon i = Icon.findById(Icon.class, category.getIconId());
            Log.i("Category Icons", "onCreate: "+i.toString());
            drawerItems.add(new PrimaryDrawerItem()
                    .withIcon(getResources().getIdentifier(i.getName(),"drawable",getPackageName()))
                    .withIconTintingEnabled(true)
                    .withIdentifier(category.getId())
                    .withBadge(String.valueOf(category.getCount()))
                    .withName(category.getName()));
        }

        drawerItems.add(new PrimaryDrawerItem().withSelectable(false).withName("Edit Labels").withIdentifier(9999));


        ExpandableDrawerItem labels = new ExpandableDrawerItem()
                .withName("Labels")
                .withSelectable(false)
                .withIsExpanded(true)
                .withSubItems(drawerItems);

        DrawerBuilder builder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.nav_header_main)
                .inflateMenu(R.menu.activity_main_drawer1)
                .addDrawerItems(new DividerDrawerItem())
                .addDrawerItems(labels)
                .addDrawerItems(new DividerDrawerItem())
                .inflateMenu(R.menu.activity_main_drawer2)
                .withSelectedItem(R.id.nav_all)
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem.equals(R.id.nav_settings)) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (drawerItem.equals(9999)) {
                            Intent intent = new Intent(MainActivity.this, EditLabelsActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return true;
                    }
                });
        builder.build();
        // navigation drawer ends here
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_help:
                return true;
            case R.id.action_backup:
                return true;
            case R.id.action_restore:
                return true;
            case R.id.action_settings:
                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);
                return true;
            case R.id.action_walkthrough:
                return true;
            case R.id.action_about:
                return true;
            case R.id.action_feedback:
                return true;
            case R.id.action_contact_us:
                return true;
            default:
                Toast.makeText(MainActivity.this, "Yet to be developed!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openNewGoalActivity(View view) {

    }

    public void openNewReminderActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
        startActivity(intent);
    }
}
