package com.urhive.scheduled.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.urhive.scheduled.R;
import com.urhive.scheduled.adapters.UseCategoryAdapter;
import com.urhive.scheduled.fragments.CategoryFragment;
import com.urhive.scheduled.fragments.RepeatTimesFragment;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;
import com.urhive.scheduled.models.Reminder;
import com.urhive.scheduled.utils.DateTimeUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private FloatingActionButton onFab, offFab;
    private EditText titleET, contentET;

    private TextView timeTV, dateTV, repeatTypeTV, advanceNotificationTV, presetTV, customizeTV;

    public static TextView categoryTV, repeatTimeTV;

    private Switch advanceNotificationSwitch;

    public static ImageView circle, categoryIcon;

    private RelativeLayout categoryRL, repeatTypeRL, repeatTimeRL, advanceNotificationRL, presetRL;

    private ExpandableHeightListView ehlv;

    private int mYear, mMonth, mHour, mMinute, mDay;
    private String mTitle;
    private String mContent;
    private int mActive = Reminder.ACTIVE;
    private String mTime;
    private String mDate;
    public static int noToRepeat = 5;

    public static Category category = Category.findById(Category.class, 1);
    private int type = Reminder.NORMAL_NOTIFICATION;
    private long inAdvanceMillis = 0;
    private boolean[] mDaysOfWeek;

    public static long inAdvanceValues[] = {900000, 86400000, 604800000};

    public static AlertDialog categoryAlertBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        allFindViewById();
        assignDefaultValues();

        repeatTimeRL.setVisibility(View.GONE);
        presetRL.setVisibility(View.GONE);

        // set date and time initially
        mDate = DateTimeUtil.getCurrentDate();
        mTime = DateTimeUtil.getCurrentTime();

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMinute = Integer.parseInt(t[1]);

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        advanceNotificationRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advanceNotificationSwitch.isChecked()) {
                    advanceNotificationSwitch.setChecked(false);
                } else {
                    advanceNotificationSwitch.setChecked(true);
                }
            }
        });

        categoryRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.view_dialog_list_view, null, false);

                ListView listView = (ListView) dialogView.findViewById(R.id.dialogListView);

                List<Category> categories = Category.find(Category.class, null, null, null, "position", null);
                List<Icon> icons = Icon.listAll(Icon.class);

                listView.setAdapter(new UseCategoryAdapter(AddReminderActivity.this, categories, icons));
                AlertDialog.Builder builder = new AlertDialog.Builder(AddReminderActivity.this)
                        .setTitle("Select Category")
                        .setNegativeButton("Add New Category", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CategoryFragment addCategoryFragment = CategoryFragment.addImmidiateCategory();
                                addCategoryFragment.show(getSupportFragmentManager(), "AddCategory");
                            }
                        })
                        .setView(dialogView);
                categoryAlertBox = builder.show();
            }
        });

        repeatTimeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatTimesFragment repeatTimeFragment = RepeatTimesFragment.showRepeatTimesFragment(noToRepeat);
                repeatTimeFragment.show(getSupportFragmentManager(),"RepeatTime");
            }
        });

        repeatTypeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
                final String[] repeatArrayCustom = getResources().getStringArray(R.array.repeat_array_custom);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddReminderActivity.this)
                        .setItems(repeatArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 5) {
                                    daysOfWeekSelector();
                                } else {
                                    if (which == 0) {

                                    }
                                }
                            }
                        })
                        .setPositiveButton("Custom", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // customPresetDialog();
                            }
                        });
                builder.show();
            }
        });

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void daysOfWeekSelector() {
        final boolean[] values = mDaysOfWeek;
        final String[] shortWeekDays = DateTimeUtil.getShortWeekDays();
        String[] weekDays = DateTimeUtil.getWeekDays();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMultiChoiceItems(weekDays, mDaysOfWeek, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                values[which] = isChecked;
            }
        }).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (Arrays.toString(values).contains("true")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Repeats On");
                    stringBuilder.append(" ");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i]) {
                            stringBuilder.append(shortWeekDays[i]);
                            stringBuilder.append(" ");
                        }
                    }

                    repeatTimeRL.setVisibility(View.VISIBLE);
                    repeatTypeTV.setText(stringBuilder);
                    Toast.makeText(AddReminderActivity.this, ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    mDaysOfWeek = values;
                } else {
                }
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void allFindViewById() {
        offFab = (FloatingActionButton) findViewById(R.id.offFab);
        onFab = (FloatingActionButton) findViewById(R.id.onFab);

        titleET = (EditText) findViewById(R.id.titleET);
        contentET = (EditText) findViewById(R.id.contentET);

        timeTV = (TextView) findViewById(R.id.setTimeTV);
        dateTV = (TextView) findViewById(R.id.setDateTV);
        repeatTypeTV = (TextView) findViewById(R.id.repeatTypeTV);
        repeatTimeTV = (TextView) findViewById(R.id.repeatTimeTV);
        advanceNotificationTV = (TextView) findViewById(R.id.setEarlyNotificationTV);
        presetTV = (TextView) findViewById(R.id.setPresetTV);
        customizeTV = (TextView) findViewById(R.id.customizeTV);
        categoryTV = (TextView) findViewById(R.id.addcategoryTV);

        circle = (ImageView) findViewById(R.id.circle);
        categoryIcon = (ImageView) findViewById(R.id.image);

        advanceNotificationSwitch = (Switch) findViewById(R.id.setEarlyNotificationSwitch);

        categoryRL = (RelativeLayout) findViewById(R.id.addCategoryRL);
        repeatTypeRL = (RelativeLayout) findViewById(R.id.repeatTypeRL);
        presetRL = (RelativeLayout) findViewById(R.id.presetRL);
        repeatTimeRL = (RelativeLayout) findViewById(R.id.repeatTimeRL);
        advanceNotificationRL = (RelativeLayout) findViewById(R.id.advanceNotificationRL);

        ehlv = (ExpandableHeightListView) findViewById(R.id.expandableheightlistviewPreset);
    }

    private void assignDefaultValues(){
        mDaysOfWeek = new boolean[7];
        Arrays.fill(mDaysOfWeek, false);
    }

    // Date & Time Picker
    // On clicking Time picker
    public void setTime(View v) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                mHour,
                mMinute,
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    // On clicking Date picker
    public void setDate(View v) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                mYear,
                --mMonth,
                mDay
        );
        dpd.setMinDate(Calendar.getInstance());
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;

        mDate = DateTimeUtil.getDate(mDay, mMonth, mYear);
        dateTV.setText(mDate);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;

        mTime = DateTimeUtil.getTime(mHour, mMinute);
        timeTV.setText(mTime);
    }

    // On clicking the active button
    public void selectFabOn(View v) {
        assert onFab != null;
        onFab.setVisibility(View.GONE);

        assert offFab != null;
        offFab.setVisibility(View.VISIBLE);
        mActive = Reminder.INACTIVE;
        System.out.println(mActive + "");
    }

    public void selectFabOff(View v) {
        assert offFab != null;
        offFab.setVisibility(View.GONE);

        assert onFab != null;
        onFab.setVisibility(View.VISIBLE);
        mActive = Reminder.ACTIVE;
        System.out.println(mActive + "");
    }
}
