package com.urhive.scheduled.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.urhive.scheduled.R;
import com.urhive.scheduled.adapters.ReminderGoalViewPagerAdapter;
import com.urhive.scheduled.fragments.ReminderFragment;
import com.urhive.scheduled.models.AlarmReminders;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Goal;
import com.urhive.scheduled.models.Icon;
import com.urhive.scheduled.models.Reminder;
import com.urhive.scheduled.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Reminder> reminderList;
    public static List<Goal> goalList;
    public static Drawer drawer;
    ViewPager viewPager;
    ReminderGoalViewPagerAdapter viewPagerAdapter;
    SharedPreferences prefs;
    FloatingActionsMenu mainFAM;
    RelativeLayout completeBG;
    List<Category> categories;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.pref, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean firstRun = prefs.getBoolean("pref_first_run", true);

        // things to do no first run
        if (firstRun) {

            Intent intent = new Intent(MainActivity.this, AppIntro.class);
            startActivity(intent);
        }

        // navigation drawer
        List<IDrawerItem> drawerItems = new ArrayList<>();
        // add labels later here via a for loop with their id as identifier and total items in them as badges
        categories = Category.find(Category.class, null, null, null, "position", null);
        Log.i("All Categories", "onCreate: " + categories.toString());

        for (Category category : categories) {
            Icon i = Icon.findById(Icon.class, category.getIconId());
            Log.i("Category Icons", "onCreate: " + i.toString());
            drawerItems.add(new PrimaryDrawerItem()
                    .withIcon(getResources().getIdentifier(i.getName(), "drawable", getPackageName()))
                    .withIconTintingEnabled(true)
                    .withIdentifier(category.getId())
                    .withTag(category)
                    .withBadge(String.valueOf(category.getCount()))
                    .withName(category.getName()));
        }

        drawerItems.add(new PrimaryDrawerItem().withSelectable(false).withName(R.string.nav_edit_labels).withIdentifier(9999));


        ExpandableDrawerItem labels = new ExpandableDrawerItem()
                .withName(R.string.nav_labels)
                .withTag(10000001)
                .withSelectable(false)
                .withIsExpanded(true)
                .withSubItems(drawerItems);

        final DrawerBuilder builder = new DrawerBuilder()
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
                        hideOption(R.id.action_delete_all_forever);
                        if (drawerItem.equals(R.id.nav_all)) {
                            reminderList = Reminder.find(Reminder.class, "status = ?", String.valueOf(Reminder.STATUS_NORMAL));
                            for (Reminder reminder : reminderList) {
                                String args[] = {String.valueOf(reminder.getId()), String.valueOf(AlarmReminders.NORMAL_ALARM_REMINDER)};
                                List<AlarmReminders> alarmReminderses = AlarmReminders.find(AlarmReminders.class, "reminder_id = ? and " +
                                        "reminder_type = ?", args);
                                Collections.sort(alarmReminderses);

                                if (alarmReminderses.isEmpty()) {
                                    reminderList.remove(reminder);
                                } else {
                                    reminder.setDate(alarmReminderses.get(0).getDate());
                                    reminder.setTime(alarmReminderses.get(0).getTime());
                                }
                            }

                            ReminderFragment.adapter.notifyListChanged(reminderList);
                            drawer.closeDrawer();
                            return true;
                        } else if (drawerItem.equals(R.id.nav_deleted)) {
                            showOption(R.id.action_delete_all_forever);
                            reminderList = Reminder.find(Reminder.class, "status = ?", String.valueOf(Reminder.STATUS_DELETED));
                            ReminderFragment.adapter.notifyListChanged(reminderList);
                            drawer.closeDrawer();
                            return true;
                        } else if (drawerItem.equals(R.id.nav_archived)) {
                            reminderList = Reminder.find(Reminder.class, "status = ?", String.valueOf(Reminder.STATUS_ARCHIEVED));
                            ReminderFragment.adapter.notifyListChanged(reminderList);
                            drawer.closeDrawer();
                            return true;
                        } else if (drawerItem.equals(R.id.nav_settings)) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (drawerItem.equals(9999)) {
                            Intent intent = new Intent(MainActivity.this, EditLabelsActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (drawerItem.equals(R.id.nav_completed)) {
                            String args[] = {};
                            reminderList = Reminder.findWithQuery(Reminder.class, "select * from reminder where no_shown = no_to_show and status = 1", args);
                            ReminderFragment.adapter.notifyListChanged(reminderList);
                            drawer.closeDrawer();
                            return true;
                        } else if (drawerItem.equals(R.id.nav_today)) {
                            reminderList = Reminder.find(Reminder.class, "status = ?", String.valueOf(Reminder.STATUS_NORMAL));
                            List<Reminder> newReminderList = new ArrayList<Reminder>();
                            String date = DateTimeUtil.getCurrentDate();
                            for (Reminder reminder : reminderList) {
                                String args[] = {date, String.valueOf(AlarmReminders.NOT_SHOWN),
                                        String.valueOf(reminder.getId()), String.valueOf(AlarmReminders.NORMAL_ALARM_REMINDER)};
                                List<AlarmReminders> alarms = AlarmReminders.find(AlarmReminders.class,
                                        "date = ? and status_shown = ? and reminder_id = ? and reminder_type = ?", args);
                                if (alarms.size() > 0) {
                                    newReminderList.add(reminder);
                                }
                            }
                            reminderList = newReminderList;
                            ReminderFragment.adapter.notifyListChanged(reminderList);
                            // Toast.makeText(MainActivity.this, "To be developed!", Toast.LENGTH_SHORT).show();
                            drawer.closeDrawer();
                            return true;
                        } else {
                            if (drawerItem.getTag().equals(10000001))
                                return true;
                            else if (drawerItem.getTag() == null) {
                                Toast.makeText(MainActivity.this, "There is some problem! Error option code: " + drawerItem.toString(), Toast.LENGTH_SHORT).show();
                                drawer.closeDrawer();
                                return true;
                            }
                            for (Category category : categories) {
                                if (drawerItem.getTag().equals(category)) {
                                    String options[] = {String.valueOf(Reminder.STATUS_NORMAL), String.valueOf(category.getId())};
                                    reminderList = Reminder.find(Reminder.class, "status = ? and category_id = ?", options);
                                    ReminderFragment.adapter.notifyListChanged(reminderList);
                                    drawer.closeDrawer();
                                    return true;
                                }
                            }
                        }
                        return true;
                    }
                });
        drawer = builder.build();

        drawer.getHeader().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Awesome!", Toast.LENGTH_SHORT).show();
            }
        });

        // navigation drawer ends here

        // Toast.makeText(MainActivity.this, ""+ Reminder.getActiveDays(), Toast.LENGTH_SHORT).show();

        reminderList = Reminder.find(Reminder.class, "status = ?", String.valueOf(Reminder.STATUS_NORMAL));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mainFAM = (FloatingActionsMenu) findViewById(R.id.mainFAM);

        viewPagerAdapter = new ReminderGoalViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        hideOption(R.id.action_delete_all_forever);
        return true;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    private void setOptionTitle(int id, String title) {
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }

    private void setOptionIcon(int id, int iconRes) {
        MenuItem item = menu.findItem(id);
        item.setIcon(iconRes);
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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_walkthrough:
                return true;
            case R.id.action_about:
                return true;
            case R.id.action_feedback:
                return true;
            case R.id.action_contact_us:
                return true;
            case R.id.action_delete_all_forever:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete all?")
                        .setMessage("Do you want to delete all reminders forever?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (Reminder reminder : reminderList) {
                                    List<AlarmReminders> alarms = AlarmReminders.find(AlarmReminders.class, "reminder_id = ?", String.valueOf(reminder.getId()));
                                    for (AlarmReminders alarm : alarms) {
                                        alarm.delete();
                                    }
                                    reminder.delete();
                                }
                                reminderList.clear();
                                ReminderFragment.adapter.notifyListChanged(reminderList);
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
                return true;
            default:
                Toast.makeText(MainActivity.this, "Yet to be developed!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openNewGoalActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AddGoalActivity.class);
        startActivity(intent);
    }

    public void openNewReminderActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
        startActivity(intent);
    }

    public void hideFAB(View view) {
    }
}
