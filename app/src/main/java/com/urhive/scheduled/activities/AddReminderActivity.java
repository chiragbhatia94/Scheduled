package com.urhive.scheduled.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.urhive.scheduled.R;
import com.urhive.scheduled.adapters.CustomExpandableListViewAdapter;
import com.urhive.scheduled.adapters.PresetExpandableListViewAdapter;
import com.urhive.scheduled.adapters.UseCategoryAdapter;
import com.urhive.scheduled.fragments.CategoryFragment;
import com.urhive.scheduled.fragments.RepeatTimesFragment;
import com.urhive.scheduled.helpers.PremiumHelper;
import com.urhive.scheduled.models.AlarmReminders;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;
import com.urhive.scheduled.models.Reminder;
import com.urhive.scheduled.utils.DateTimeUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    public static TextView categoryTV, repeatTimeTV;
    public static ImageView circle, categoryIcon;
    public static int noToRepeat = 5;
    public static Category category = Category.findById(Category.class, 1);
    public static long inAdvanceValues[] = {900000, 86400000, 604800000};
    public static AlertDialog categoryAlertBox;
    public static TextView repeatTypeTV;
    public static RelativeLayout presetRL, repeatTypeRL, repeatTimeRL;
    public static ExpandableHeightListView ehlv;
    public static int repeatType = 0;
    public static Boolean customizing = Boolean.FALSE;
    private static int typeNeutral;

    private MaterialCalendarView mcv;
    private List<CalendarDay> dates;
    private AlertDialog calendarAlert;
    private HashMap<CalendarDay, String> customlist;
    private AlertDialog.Builder repeatArrayBuilder, repeatCustomArrayBuilder;
    private FloatingActionButton onFab, offFab;
    private EditText titleET, contentET;
    private TextView timeTV, dateTV, advanceNotificationTV, presetTV, customizeTV, notiTypeTV;
    private Button addCustomReminderFab;
    private String mTime;
    private Switch advanceNotificationSwitch;
    private RelativeLayout categoryRL, advanceNotificationRL, notiTypeRL;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private String mTitle;
    private String mContent;
    private int notiType = Reminder.TYPE_NOTIFICATION;
    private int mActive = Reminder.ACTIVE;
    private String mDate;
    private String mmDate, mmTime;
    private long inAdvanceMillis = 0;
    private boolean[] mDaysOfWeek;
    private List<AlarmReminders> defaultPresetList;

    private PresetExpandableListViewAdapter presetExpandableListViewAdapter;
    private CustomExpandableListViewAdapter customExpandableListViewAdapter;

    public static void resetToDoNotRepeat() {
        presetRL.setVisibility(View.GONE);
        ehlv.setVisibility(View.GONE);
        customizing = Boolean.FALSE;
        repeatType = 0;
        repeatTypeTV.setText("Does not repeat");
        repeatTimeRL.setVisibility(View.GONE);
    }

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
                final String[] advanceNotificationArray = getResources().getStringArray(R.array.advance_notification_array);
                if (advanceNotificationSwitch.isChecked()) {
                    advanceNotificationSwitch.setChecked(false);
                    inAdvanceMillis = 0;
                    advanceNotificationTV.setText("Do not remind early");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddReminderActivity.this)
                            .setItems(advanceNotificationArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    inAdvanceMillis = inAdvanceValues[which];
                                    advanceNotificationTV.setText(advanceNotificationArray[which]);
                                    advanceNotificationSwitch.setChecked(true);
                                }
                            });
                    builder.show();
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
                        .setPositiveButton("Add New Category", new DialogInterface.OnClickListener() {
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
                repeatTimeFragment.show(getSupportFragmentManager(), "RepeatTime");
            }
        });

        repeatTypeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
                final String[] repeatArrayCustom = getResources().getStringArray(R.array.repeat_array_custom);

                repeatArrayBuilder = new AlertDialog.Builder(AddReminderActivity.this)
                        .setItems(repeatArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                repeatType = which;
                                defaultPresetList.clear();

                                if (which == Reminder.SPECIFIC_DAY_OF_WEEK) {
                                    presetRL.setVisibility(View.GONE);
                                    ehlv.setVisibility(View.GONE);
                                    daysOfWeekSelector();
                                } else if (which == Reminder.REVISION_PRESET) {
                                    repeatTypeTV.setText(repeatArray[repeatType]);
                                    presetRL.setVisibility(View.VISIBLE);
                                    customizeTV.setVisibility(View.VISIBLE);
                                    presetTV.setText("Default");
                                    ehlvSetAdapter();
                                    ehlv.setVisibility(View.VISIBLE);
                                } else {
                                    repeatTypeTV.setText(repeatArray[repeatType]);
                                    presetRL.setVisibility(View.GONE);
                                    ehlv.setVisibility(View.GONE);
                                }

                                repeatTimeRL.setVisibility(View.VISIBLE);
                                // changes for no to show or repeatTimeRL
                                if (which == Reminder.DO_NOT_REPEAT || which == Reminder.REVISION_PRESET) {
                                    repeatTimeRL.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setPositiveButton("Custom", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                customPresetDialog(repeatArrayCustom);
                            }
                        });
                repeatArrayBuilder.show();
            }
        });

        customizeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PremiumHelper.checkPremium(AddReminderActivity.this, "Customize Presets", "Buy Premium to customize presets & add or remove reminders!")) {
                    if (!customizing) {
                        customizing = Boolean.TRUE;
                        addCustomReminderFab.setVisibility(View.VISIBLE);
                        customizeTV.setText("Use Default");
                        presetTV.setText("Custom");
                        presetExpandableListViewAdapter.notifyDataSetChanged();
                    } else {
                        addCustomReminderFab.setVisibility(View.GONE);
                        customizing = Boolean.FALSE;
                        customizeTV.setText("Customize");
                        presetTV.setText("Default");
                        createDefaultRevisionPresetList();
                        presetExpandableListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        presetRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ehlv.getVisibility() == View.VISIBLE) {
                    ehlv.setVisibility(View.GONE);
                } else {
                    ehlv.setVisibility(View.VISIBLE);
                }
            }
        });

        notiTypeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddReminderActivity.this)
                        .setTitle("Select Reminder Type")
                        .setSingleChoiceItems(new String[]{"Notication", "Alarm"}, notiType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notiType = which;
                                if (which == Reminder.TYPE_NOTIFICATION) {
                                    notiTypeTV.setText("Notification");
                                } else if (which == Reminder.TYPE_ALARM) {
                                    notiTypeTV.setText("Alarm");
                                }
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        });
    }

    public void addPreset(View view) {
        int size = defaultPresetList.size();

        if (size > 0) {
            mmDate = defaultPresetList.get(size - 1).getDate();
            mmTime = defaultPresetList.get(size - 1).getTime();
        } else {
            mmDate = defaultPresetList.get(0).getDate();
            mmTime = defaultPresetList.get(0).getTime();
        }

        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMinute = Integer.parseInt(t[1]);

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                mYear,
                --mMonth,
                mDay
        );
        dpd.setMinDate(Calendar.getInstance());
        dpd.show(getFragmentManager(), "Datepickerdialog");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                mDay = dayOfMonth;
                mMonth = monthOfYear;
                mYear = year;

                mmDate = DateTimeUtil.getDate(mDay, mMonth, mYear);

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddReminderActivity.this,
                        mHour,
                        mMinute,
                        false
                );
                tpd.setThemeDark(false);
                tpd.show(getFragmentManager(), "Timepickerdialog");
                tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;

                        mmTime = DateTimeUtil.getTime(mHour, mMinute);

                        int size = defaultPresetList.size();
                        defaultPresetList.add(new AlarmReminders(size + 1, mmDate, mmTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
                        AlarmReminders.sortCustomReminderListByDateTimeAndArrangeByNumber(defaultPresetList);
                        if (repeatType == Reminder.REVISION_PRESET) {
                            presetExpandableListViewAdapter.notifyDataSetChanged();
                        } else if (repeatType == Reminder.CUSTOM) {
                            customExpandableListViewAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    private void ehlvSetAdapter() {
        createDefaultRevisionPresetList();
        presetExpandableListViewAdapter = new PresetExpandableListViewAdapter(AddReminderActivity.this, defaultPresetList, getFragmentManager());
        ehlv.setAdapter(presetExpandableListViewAdapter);
        ehlv.setExpanded(true);
    }

    private void createDefaultRevisionPresetList() {
        if (!defaultPresetList.isEmpty()) {
            defaultPresetList.clear();
        }

        defaultPresetList.add(new AlarmReminders(1, DateTimeUtil.addDaysToDate(mDate, 0), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        defaultPresetList.add(new AlarmReminders(2, DateTimeUtil.addDaysToDate(mDate, 1), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        defaultPresetList.add(new AlarmReminders(3, DateTimeUtil.addDaysToDate(mDate, 7), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        defaultPresetList.add(new AlarmReminders(4, DateTimeUtil.addDaysToDate(mDate, 30), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        defaultPresetList.add(new AlarmReminders(5, DateTimeUtil.addDaysToDate(mDate, 90), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
    }

    public void customPresetDialog(final String repeatCustomArray[]) {
        repeatCustomArrayBuilder = new AlertDialog.Builder(AddReminderActivity.this)
                .setItems(repeatCustomArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customlist = new HashMap<CalendarDay, String>();
                        repeatType = which + 7;
                        repeatTypeTV.setText(repeatCustomArray[which]);
                        presetRL.setVisibility(View.GONE);
                        ehlv.setVisibility(View.GONE);

                        customizing = Boolean.TRUE;

                        repeatTimeRL.setVisibility(View.VISIBLE);
                        if (repeatType == Reminder.CUSTOM) {
                            // changes for no to show or repeatTimeRL
                            repeatTimeRL.setVisibility(View.GONE);

                            // code for custom
                            typeNeutral = 0;
                            // 0 is range
                            // 1 is discrete

                            String d[] = mDate.split("/");
                            mDay = Integer.parseInt(d[0]);
                            mMonth = Integer.parseInt(d[1]);
                            mYear = Integer.parseInt(d[2]);

                            mcv = new MaterialCalendarView(AddReminderActivity.this);
                            mcv.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
                            mcv.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
                            mcv.state().edit()
                                    .setMinimumDate(CalendarDay.from(mYear, mMonth, mDay))
                                    .commit();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddReminderActivity.this);
                            Boolean type = preferences.getBoolean("reminderSelection", false);

                            if (type == PremiumHelper.CUSTOM_TYPE_DISCRETE) {
                                typeNeutral = 1;
                                mcv.setOnDateChangedListener(new OnDateSelectedListener() {
                                    @Override
                                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {
                                        if (selected) {
                                            String t[] = mTime.split(":");
                                            mHour = Integer.parseInt(t[0]);
                                            mMinute = Integer.parseInt(t[1]);

                                            final TimePickerDialog tpd = TimePickerDialog.newInstance(
                                                    AddReminderActivity.this,
                                                    mHour,
                                                    mMinute,
                                                    false
                                            );

                                            tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                                    customlist.put(date, hourOfDay + ":" + minute);
                                                }
                                            });

                                            tpd.setThemeDark(false);
                                            tpd.show(getFragmentManager(), "Timepickerdialog");
                                        } else {
                                            customlist.remove(date);
                                        }
                                    }
                                });
                            } else {
                                typeNeutral = 0;
                            }

                            calendarAlert = new AlertDialog.Builder(AddReminderActivity.this)
                                    .setView(mcv)
                                    .setNeutralButton("Discrete/Range", null)
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("Okay", null)
                                    .setCancelable(false)
                                    .create();

                            calendarAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    Button neutral = calendarAlert.getButton(Dialog.BUTTON_NEUTRAL);
                                    Button negative = calendarAlert.getButton(Dialog.BUTTON_NEGATIVE);
                                    Button positive = calendarAlert.getButton(Dialog.BUTTON_POSITIVE);

                                    positive.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            customizeTV.setVisibility(View.GONE);
                                            presetTV.setText("Tap to change notification time");

                                            getNSetCustomDates(mcv);
                                            calendarAlert.dismiss();
                                        }
                                    });

                                    negative.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetToDoNotRepeat();
                                            calendarAlert.dismiss();
                                        }
                                    });

                                    neutral.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            switchMCVWorking();
                                        }
                                    });
                                }
                            });
                            calendarAlert.show();
                        }
                    }
                })
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repeatArrayBuilder.show();
                    }
                });
        repeatCustomArrayBuilder.show();
    }

    public void switchMCVWorking() {
        if (typeNeutral == 0) {
            typeNeutral = 1;
            Toast.makeText(AddReminderActivity.this, "Select Discrete Reminders", Toast.LENGTH_SHORT).show();
            mcv.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {
                    // click to open a timepicker
                    if (selected) {
                        String t[] = mTime.split(":");
                        mHour = Integer.parseInt(t[0]);
                        mMinute = Integer.parseInt(t[1]);

                        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                AddReminderActivity.this,
                                mHour,
                                mMinute,
                                false
                        );

                        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                customlist.put(date, hourOfDay + ":" + minute);
                            }
                        });

                        tpd.setThemeDark(false);
                        tpd.show(getFragmentManager(), "Timepickerdialog");
                    } else {
                        customlist.remove(date);
                    }
                }
            });
            calendarAlert.setButton(DialogInterface.BUTTON_NEUTRAL, "Select Range", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        } else {
            typeNeutral = 0;
            Toast.makeText(AddReminderActivity.this, "Select Range of Reminders", Toast.LENGTH_SHORT).show();
            mcv.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                }
            });
            calendarAlert.setButton(DialogInterface.BUTTON_NEUTRAL, "Select Discrete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }

    public void getNSetCustomDates(MaterialCalendarView mcv) {
        dates = mcv.getSelectedDates();
        if (dates.isEmpty()) {
            resetToDoNotRepeat();
            return;
        }

        addCustomReminderFab.setVisibility(View.VISIBLE);
        defaultPresetList.clear();
        int i = 1;
        for (CalendarDay d : dates) {
            if (customlist.containsKey(d)) {
                String time = customlist.get(d);
                String t[] = time.split(":");
                int h = Integer.parseInt(t[0]);
                int m = Integer.parseInt(t[1]);
                defaultPresetList.add(new AlarmReminders(i, DateTimeUtil.getDate(d.getDay(), d.getMonth(), d.getYear()), DateTimeUtil.getTime(h, m), AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
            } else {
                defaultPresetList.add(new AlarmReminders(i, DateTimeUtil.getDate(d.getDay(), d.getMonth(), d.getYear()), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
            }
            i++;
        }

        ehlv.setExpanded(true);
        customExpandableListViewAdapter = new CustomExpandableListViewAdapter(AddReminderActivity.this, defaultPresetList, getFragmentManager());
        ehlv.setAdapter(customExpandableListViewAdapter);
        ehlv.setVisibility(View.VISIBLE);
        presetRL.setVisibility(View.VISIBLE);
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
                    Toast.makeText(AddReminderActivity.this, "" + stringBuilder, Toast.LENGTH_SHORT).show();
                    mDaysOfWeek = values;
                } else {
                    repeatType = 0;
                    repeatTypeTV.setText("Do not repeat");
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

        addCustomReminderFab = (Button) findViewById(R.id.addCustomReminderFab);

        timeTV = (TextView) findViewById(R.id.setTimeTV);
        dateTV = (TextView) findViewById(R.id.setDateTV);
        repeatTypeTV = (TextView) findViewById(R.id.repeatTypeTV);
        repeatTimeTV = (TextView) findViewById(R.id.repeatTimeTV);
        advanceNotificationTV = (TextView) findViewById(R.id.setEarlyNotificationTV);
        presetTV = (TextView) findViewById(R.id.setPresetTV);
        customizeTV = (TextView) findViewById(R.id.customizeTV);

        categoryTV = (TextView) findViewById(R.id.addcategoryTV);
        notiTypeTV = (TextView) findViewById(R.id.notiTypeTV);

        circle = (ImageView) findViewById(R.id.circle);
        categoryIcon = (ImageView) findViewById(R.id.image);

        advanceNotificationSwitch = (Switch) findViewById(R.id.setEarlyNotificationSwitch);

        categoryRL = (RelativeLayout) findViewById(R.id.addCategoryRL);
        repeatTypeRL = (RelativeLayout) findViewById(R.id.repeatTypeRL);
        presetRL = (RelativeLayout) findViewById(R.id.presetRL);
        repeatTimeRL = (RelativeLayout) findViewById(R.id.repeatTimeRL);
        advanceNotificationRL = (RelativeLayout) findViewById(R.id.advanceNotificationRL);
        notiTypeRL = (RelativeLayout) findViewById(R.id.notiTypeRL);

        ehlv = (ExpandableHeightListView) findViewById(R.id.expandableheightlistviewPreset);
    }

    private void assignDefaultValues() {
        mDaysOfWeek = new boolean[7];
        Arrays.fill(mDaysOfWeek, false);
        defaultPresetList = new ArrayList<>();
    }

    // Date & Time Picker
    // On clicking Time picker
    public void setTime(View v) {
        if (timeTV.getText().toString().equals("Now")) {
            mTime = DateTimeUtil.getCurrentTime();
        }

        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMinute = Integer.parseInt(t[1]);
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                mHour,
                mMinute,
                false
        );

        if (repeatType == Reminder.REVISION_PRESET || repeatType == Reminder.CUSTOM) {
            if (!defaultPresetList.isEmpty()) {
                for (AlarmReminders reminder : defaultPresetList) {
                    if (DateTimeUtil.isInPast(reminder.getDate(), reminder.getTime())) {
                        Toast.makeText(AddReminderActivity.this, "Custom Reminders are set in past!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }

        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    // On clicking Date picker
    public void setDate(View v) {
        if (dateTV.getText().toString().equals("Today")) {
            mDate = DateTimeUtil.getCurrentDate();
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                mYear,
                --mMonth,
                mDay
        );

        if (repeatType == Reminder.REVISION_PRESET || repeatType == Reminder.CUSTOM) {
            if (!defaultPresetList.isEmpty()) {
                for (AlarmReminders reminder : defaultPresetList) {
                    if (DateTimeUtil.isInPast(reminder.getDate(), reminder.getTime())) {
                        Toast.makeText(AddReminderActivity.this, "Custom Reminders are set in past!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_reminder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_discard) {
            onBackPressed();
            Toast.makeText(AddReminderActivity.this, "Discarded!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_done) {
            saveReminder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveReminder() {
        mTitle = titleET.getText().toString();
        if (mTitle.isEmpty()) {
            titleET.setError("Title cannot be empty!");
            Toast.makeText(AddReminderActivity.this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        mContent = contentET.getText().toString();
        long categoryId = category.getId();

        if (repeatType == Reminder.DO_NOT_REPEAT) {

        }
    }
}
