package com.urhive.scheduled.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.urhive.scheduled.models.DayOfWeek;
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

    public static TextView categoryTV, noToShowTV;
    public static ImageView circle, categoryIcon;
    public static AlertDialog categoryAlertBox;
    public static Category category;
    public static int noToShow;
    public static Boolean customizing = Boolean.FALSE;

    private static TextView repeatTypeTV;
    private static RelativeLayout presetRL, repeatTypeRL, repeatTimeRL;
    private static ExpandableHeightListView ehlv;
    private static int typeNeutral, repeatType = 0;

    private AlertDialog calendarAlert;
    private AlertDialog.Builder repeatArrayBuilder, repeatCustomArrayBuilder;
    private MaterialCalendarView mcv;
    private FloatingActionButton onFab, offFab;
    private EditText titleET, contentET;
    private TextView timeTV, dateTV, advanceNotiTV, presetTV, customizeTV, notiTypeTV;
    private Button addReminder;
    private Switch advanceNotiSwitch;

    private RelativeLayout categoryRL, advanceNotiRL, notiTypeRL;

    private PresetExpandableListViewAdapter presetExpandableListViewAdapter;
    private CustomExpandableListViewAdapter customExpandableListViewAdapter;

    private String mTime, mDate, mmDate, mmTime;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private String mTitle, mContent;
    private String[] repeatArray, repeatArrayCustom;
    private int notiType, mActive = Reminder.ACTIVE;
    private long inAdvanceMillis = 0;

    private boolean[] mDaysOfWeek;
    private HashMap<CalendarDay, String> customList;
    private List<AlarmReminders> customReminderList;

    public static void resetToDoNotRepeat() {
        presetRL.setVisibility(View.GONE);
        ehlv.setVisibility(View.GONE);
        customizing = Boolean.FALSE;
        repeatType = 0;
        repeatTypeTV.setText(R.string.do_not_repeat);
        repeatTimeRL.setVisibility(View.GONE);
    }

    private void setDateTimeVariables() {
        setDateVariables();
        setTimeVariables();
    }

    private void setDateVariables() {
        String d[] = mDate.split("/");
        mDay = Integer.parseInt(d[0]);
        mMonth = Integer.parseInt(d[1]);
        mYear = Integer.parseInt(d[2]);
    }

    private void setTimeVariables() {
        String t[] = mTime.split(":");
        mHour = Integer.parseInt(t[0]);
        mMinute = Integer.parseInt(t[1]);
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

        setDateTimeVariables();

        advanceNotiRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] advanceNotificationArray = getResources().getStringArray(R.array.advance_notification_array);
                if (advanceNotiSwitch.isChecked()) {
                    advanceNotiSwitch.setChecked(false);
                    inAdvanceMillis = 0;
                    advanceNotiTV.setText(R.string.do_not_remind_early);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddReminderActivity.this)
                            .setItems(advanceNotificationArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    inAdvanceMillis = Reminder.inAdvanceValues[which];
                                    advanceNotiTV.setText(advanceNotificationArray[which]);
                                    advanceNotiSwitch.setChecked(true);
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
                        .setTitle(R.string.select_category)
                        .setPositiveButton(R.string.add_new_category, new DialogInterface.OnClickListener() {
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
                RepeatTimesFragment repeatTimeFragment = RepeatTimesFragment.showRepeatTimesFragment(noToShow);
                repeatTimeFragment.show(getSupportFragmentManager(), "RepeatTime");
            }
        });

        repeatTypeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatArrayBuilder = new AlertDialog.Builder(AddReminderActivity.this)
                        .setItems(repeatArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                repeatType = which;
                                customReminderList.clear();

                                if (which == Reminder.SPECIFIC_DAY_OF_WEEK) {
                                    presetRL.setVisibility(View.GONE);
                                    ehlv.setVisibility(View.GONE);
                                    daysOfWeekSelector();
                                } else if (which == Reminder.REVISION_PRESET) {
                                    repeatTypeTV.setText(repeatArray[repeatType]);
                                    presetRL.setVisibility(View.VISIBLE);
                                    customizeTV.setVisibility(View.VISIBLE);
                                    presetTV.setText(R.string.default_string);
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
                        addReminder.setVisibility(View.VISIBLE);
                        customizeTV.setText(R.string.use_default);
                        presetTV.setText(R.string.custom);
                        presetExpandableListViewAdapter.notifyDataSetChanged();
                    } else {
                        addReminder.setVisibility(View.GONE);
                        customizing = Boolean.FALSE;
                        customizeTV.setText(R.string.customize);
                        presetTV.setText(R.string.default_string);
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
                        .setTitle(R.string.select_reminder_type)
                        .setSingleChoiceItems(new String[]{getResources().getString(R.string.notification), getResources().getString(R.string.alarm)}, notiType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notiType = which;
                                if (which == Reminder.TYPE_NOTIFICATION) {
                                    notiTypeTV.setText(R.string.notification);
                                } else if (which == Reminder.TYPE_ALARM) {
                                    notiTypeTV.setText(R.string.alarm);
                                }
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        });
    }

    public void addPreset(View view) {
        int size = customReminderList.size();

        if (size > 0) {
            mmDate = customReminderList.get(size - 1).getDate();
            mmTime = customReminderList.get(size - 1).getTime();
        } else {
            mmDate = customReminderList.get(0).getDate();
            mmTime = customReminderList.get(0).getTime();
        }

        setDateTimeVariables();

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

                        int size = customReminderList.size();
                        customReminderList.add(new AlarmReminders(size + 1, mmDate, mmTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
                        AlarmReminders.sortCustomReminderListByDateTimeAndArrangeByNumber(customReminderList);
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
        presetExpandableListViewAdapter = new PresetExpandableListViewAdapter(AddReminderActivity.this, customReminderList, getFragmentManager());
        ehlv.setAdapter(presetExpandableListViewAdapter);
        ehlv.setExpanded(true);
    }

    private void createDefaultRevisionPresetList() {
        if (!customReminderList.isEmpty()) {
            customReminderList.clear();
        }

        customReminderList.add(new AlarmReminders(1, DateTimeUtil.addDaysToDate(mDate, 0), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        customReminderList.add(new AlarmReminders(2, DateTimeUtil.addDaysToDate(mDate, 1), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        customReminderList.add(new AlarmReminders(3, DateTimeUtil.addDaysToDate(mDate, 7), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        customReminderList.add(new AlarmReminders(4, DateTimeUtil.addDaysToDate(mDate, 30), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
        customReminderList.add(new AlarmReminders(5, DateTimeUtil.addDaysToDate(mDate, 90), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
    }

    public void customPresetDialog(final String repeatCustomArray[]) {
        repeatCustomArrayBuilder = new AlertDialog.Builder(AddReminderActivity.this)
                .setItems(repeatCustomArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customList = new HashMap<>();
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

                            setDateVariables();

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
                                            setTimeVariables();

                                            final TimePickerDialog tpd = TimePickerDialog.newInstance(
                                                    AddReminderActivity.this,
                                                    mHour,
                                                    mMinute,
                                                    false
                                            );

                                            tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                                    customList.put(date, hourOfDay + ":" + minute);
                                                }
                                            });

                                            tpd.setThemeDark(false);
                                            tpd.show(getFragmentManager(), "Timepickerdialog");
                                        } else {
                                            customList.remove(date);
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
                                            presetTV.setText(R.string.tap_to_change_notification_time);

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
                        setTimeVariables();

                        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                AddReminderActivity.this,
                                mHour,
                                mMinute,
                                false
                        );

                        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                customList.put(date, hourOfDay + ":" + minute);
                            }
                        });

                        tpd.setThemeDark(false);
                        tpd.show(getFragmentManager(), "Timepickerdialog");
                    } else {
                        customList.remove(date);
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
        List<CalendarDay> dates = mcv.getSelectedDates();
        if (dates.isEmpty()) {
            resetToDoNotRepeat();
            return;
        }

        addReminder.setVisibility(View.VISIBLE);
        customReminderList.clear();
        int i = 1;
        for (CalendarDay d : dates) {
            if (customList.containsKey(d)) {
                String time = customList.get(d);
                String t[] = time.split(":");
                int h = Integer.parseInt(t[0]);
                int m = Integer.parseInt(t[1]);
                customReminderList.add(new AlarmReminders(i, DateTimeUtil.getDate(d.getDay(), d.getMonth(), d.getYear()), DateTimeUtil.getTime(h, m), AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
            } else {
                customReminderList.add(new AlarmReminders(i, DateTimeUtil.getDate(d.getDay(), d.getMonth(), d.getYear()), mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI));
            }
            i++;
        }

        ehlv.setExpanded(true);
        customExpandableListViewAdapter = new CustomExpandableListViewAdapter(AddReminderActivity.this, customReminderList, getFragmentManager());
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
                    repeatTypeTV.setText(R.string.do_not_repeat);
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
        noToShowTV = (TextView) findViewById(R.id.noToShowTV);
        advanceNotiTV = (TextView) findViewById(R.id.advanceNotiTV);
        presetTV = (TextView) findViewById(R.id.setPresetTV);
        customizeTV = (TextView) findViewById(R.id.customizeTV);
        categoryTV = (TextView) findViewById(R.id.categoryTV);
        notiTypeTV = (TextView) findViewById(R.id.notiTypeTV);

        circle = (ImageView) findViewById(R.id.circle);
        categoryIcon = (ImageView) findViewById(R.id.image);

        advanceNotiSwitch = (Switch) findViewById(R.id.advanceNotiSwitch);

        categoryRL = (RelativeLayout) findViewById(R.id.addCategoryRL);
        repeatTypeRL = (RelativeLayout) findViewById(R.id.repeatTypeRL);
        presetRL = (RelativeLayout) findViewById(R.id.presetRL);
        repeatTimeRL = (RelativeLayout) findViewById(R.id.noToShowRL);
        advanceNotiRL = (RelativeLayout) findViewById(R.id.advanceNotificationRL);
        notiTypeRL = (RelativeLayout) findViewById(R.id.notiTypeRL);

        addReminder = (Button) findViewById(R.id.addReminder);
        ehlv = (ExpandableHeightListView) findViewById(R.id.expandableheightlistviewPreset);
    }

    private void assignDefaultValues() {
        category = Category.findById(Category.class, 1);
        noToShow = 5;
        mDaysOfWeek = new boolean[7];
        Arrays.fill(mDaysOfWeek, false);
        customReminderList = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddReminderActivity.this);
        notiType = Integer.parseInt(preferences.getString("notiTypePref", String.valueOf(Reminder.TYPE_NOTIFICATION)));

        repeatArray = getResources().getStringArray(R.array.repeat_array);
        repeatArrayCustom = getResources().getStringArray(R.array.repeat_array_custom);

        if (notiType == Reminder.TYPE_NOTIFICATION) {
            notiTypeTV.setText(R.string.notification);
        } else if (notiType == Reminder.TYPE_ALARM) {
            notiTypeTV.setText(R.string.alarm);
        }
    }

    // Date & Time Picker
    // On clicking Time picker
    public void setTime(View v) {
        if (timeTV.getText().toString().equals(R.string.now)) {
            mTime = DateTimeUtil.getCurrentTime();
        }

        setTimeVariables();

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                mHour,
                mMinute,
                false
        );
        if (repeatType == Reminder.REVISION_PRESET || repeatType == Reminder.CUSTOM) {
            if (isAnyCustomRemindersInPast()) {
                return;
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
            if (isAnyCustomRemindersInPast()) {
                return;
            }
        }

        dpd.setMinDate(Calendar.getInstance());
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private boolean isAnyCustomRemindersInPast() {
        if (!customReminderList.isEmpty()) {
            for (AlarmReminders reminder : customReminderList) {
                if (DateTimeUtil.isInPast(reminder.getDate(), reminder.getTime())) {
                    Toast.makeText(AddReminderActivity.this, R.string.custom_reminders_are_set_in_past, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }
        return false;
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
        /* These are already set
        * mDate
        * mTime
        * mActive
        * */

        if (dateTV.getText().toString().equals("Today")) {
            mDate = DateTimeUtil.getCurrentDate();
            setDateVariables();
        }

        if (timeTV.getText().toString().equals("Now")) {
            mTime = DateTimeUtil.getCurrentTime();
            setTimeVariables();
        }

        final long categoryId = category.getId();

        // noToShow
        if (repeatType == Reminder.DO_NOT_REPEAT) {
            noToShow = 1;
        } else if (repeatType == Reminder.REVISION_PRESET || repeatType == Reminder.CUSTOM) {
            noToShow = customReminderList.size();
        }
        /* in the following cases noToShow is already set
        * Reminder.EVERY_DAY
        * Reminder.EVERY_MONTH
        * Reminder.EVERY_YEAR
        * Reminder.SPECIFIC_DAY_OF_WEEK
        * Reminder.ALTERNATE_DAYS
        * Reminder.MWF_TTS_ALTERNATE
        * */

        final int noShown = 0;

        /* these are already set
        * repeatType
        * inAdvanceMillis
        * */

        final int status = Reminder.STATUS_NORMAL;

        // notiType already set

        // setAlarms
        if (repeatType == Reminder.DO_NOT_REPEAT) {
            // creating Reminder Object
            if (DateTimeUtil.isInPast(mDate, mTime)) {
                Toast.makeText(AddReminderActivity.this, "Reminder in past!\nTry Again!", Toast.LENGTH_SHORT).show();
                return;
            }

            Reminder reminder = new Reminder(mTitle, mContent, mDate, mTime, mActive, categoryId, noToShow, noShown, repeatType, inAdvanceMillis, status, notiType);

            long reminderId = reminder.save();
            reminder.setId(reminderId);

            if (inAdvanceMillis != 0) {
                String[] dt = DateTimeUtil.calcAdavanceNotiDateTime(mDate, mTime, inAdvanceMillis);
                if (!DateTimeUtil.isInPast(dt[0], dt[1])) {
                    AlarmReminders advanceNoti = new AlarmReminders(reminderId, 1, dt[0], dt[1], AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI);
                    advanceNoti.save();
                }
            }
            AlarmReminders onlyAlarm = new AlarmReminders(reminderId, 1, mDate, mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ALARM);
            onlyAlarm.save();

            Intent intent = new Intent(AddReminderActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(AddReminderActivity.this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
        } else if (repeatType == Reminder.EVERY_DAY ||
                repeatType == Reminder.EVERY_WEEK ||
                repeatType == Reminder.EVERY_MONTH ||
                repeatType == Reminder.EVERY_YEAR ||
                repeatType == Reminder.ALTERNATE_DAYS) {
            final Reminder reminder = new Reminder(mTitle, mContent, mDate, mTime, mActive, categoryId, noToShow, noShown, repeatType, inAdvanceMillis, status, notiType);

            if (DateTimeUtil.isInPast(mDate, mTime)) {
                /*
                * show a alert dialog asking user to set alarm from next repeating alarm or to change date and time
                * */
                AlertDialog.Builder pastReminderDialog = new AlertDialog.Builder(AddReminderActivity.this)
                        .setTitle("Reminder in past!")
                        .setMessage("Todays alarm time is already passed! Want to reschedule alarm?")
                        .setPositiveButton("Reschedule", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setNegativeButton("Skip Today", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long reminderId = reminder.save();
                                reminder.setId(reminderId);
                                String nextDate = calculateNextReminder(reminder.getDate(), reminder.getRepeatType());
                                AlarmReminders nextReminder = new AlarmReminders(reminderId, 1, nextDate, mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ALARM);
                                nextReminder.save();
                                if (inAdvanceMillis != 0) {
                                    String[] dt = DateTimeUtil.calcAdavanceNotiDateTime(nextDate, mTime, inAdvanceMillis);
                                    if (!DateTimeUtil.isInPast(dt[0], dt[1])) {
                                        AlarmReminders advanceNoti = new AlarmReminders(reminderId, 1, dt[0], dt[1], AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI);
                                        advanceNoti.save();
                                    }
                                }
                                Intent intent = new Intent(AddReminderActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(AddReminderActivity.this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Show Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long reminderId = reminder.save();
                                reminder.setId(reminderId);
                                AlarmReminders nowReminder = new AlarmReminders(reminderId, 1, mDate, DateTimeUtil.getCurrentTime(), AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ALARM);
                                nowReminder.save();
                                // no advance noti

                                Intent intent = new Intent(AddReminderActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(AddReminderActivity.this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
                            }
                        });
                pastReminderDialog.show();
            } else {
                // creating Reminder Object
                long reminderId = reminder.save();
                reminder.setId(reminderId);

                AlarmReminders firstAlarm = new AlarmReminders(reminderId, 1, mDate, mTime, AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ALARM);
                firstAlarm.save();

                if (inAdvanceMillis != 0) {
                    String[] dt = DateTimeUtil.calcAdavanceNotiDateTime(mDate, mTime, inAdvanceMillis);
                    if (!DateTimeUtil.isInPast(dt[0], dt[1])) {
                        AlarmReminders advanceNoti = new AlarmReminders(reminderId, 1, dt[0], dt[1], AlarmReminders.NOT_SHOWN, AlarmReminders.TYPE_ADVANCED_NOTI);
                        advanceNoti.save();
                    }
                }
                Intent intent = new Intent(AddReminderActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(AddReminderActivity.this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
            }
        } else if (repeatType == Reminder.REVISION_PRESET || repeatType == Reminder.CUSTOM) {
            if (isAnyCustomRemindersInPast()) {
                Toast.makeText(AddReminderActivity.this, "Custom Reminder in past!", Toast.LENGTH_SHORT).show();
                return;
            }
            // creating Reminder Object
            Reminder reminder = new Reminder(mTitle, mContent, mDate, mTime, mActive, categoryId, noToShow, noShown, repeatType, inAdvanceMillis, status, notiType);

            long reminderId = reminder.save();
            reminder.setId(reminderId);

            for (AlarmReminders alarm : customReminderList) {
                alarm.setAlarmType(notiType);
                alarm.setReminderId(reminderId);
                alarm.save();
            }
            Intent intent = new Intent(AddReminderActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(AddReminderActivity.this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
        } else if (repeatType == Reminder.SPECIFIC_DAY_OF_WEEK) {
            // creating Reminder Object
            Reminder reminder = new Reminder(mTitle, mContent, mDate, mTime, mActive, categoryId, noToShow, noShown, repeatType, inAdvanceMillis, status, notiType);

            long reminderId = reminder.save();
            reminder.setId(reminderId);

            DayOfWeek dow = new DayOfWeek(mDaysOfWeek);
            dow.save();

            Calendar cal = Calendar.getInstance();
            int dayofweek = cal.get(Calendar.DAY_OF_WEEK) - 1;

            String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            int found = 0;
            for (int i = 0; i < mDaysOfWeek.length; i++) {
                if (mDaysOfWeek[i]) {
                    if (dayofweek <= i) {
                        Toast.makeText(AddReminderActivity.this, "set alarm of " + daysOfWeek[i], Toast.LENGTH_SHORT).show();
                        found = 1;
                    }
                }
            }
            if (found == 0) {
                Toast.makeText(AddReminderActivity.this, "Alarm wont run this week!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String calculateNextReminder(String date, int RepeatType) {
        String d[] = date.split("/");
        int day = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]);
        int year = Integer.parseInt(d[2]);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, --month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        switch (repeatType) {
            case Reminder.EVERY_DAY:
                calendar.add(Calendar.DATE, 1);
                break;
            case Reminder.EVERY_WEEK:
                calendar.add(Calendar.DATE, 7);
                break;
            case Reminder.EVERY_MONTH:
                calendar.add(Calendar.MONTH, 1);
                break;
            case Reminder.EVERY_YEAR:
                calendar.add(Calendar.YEAR, 1);
                break;
            case Reminder.ALTERNATE_DAYS:
                calendar.add(Calendar.DATE, 2);
                break;
        }
        date = DateTimeUtil.dateFormat.format(calendar.getTime());
        return date;
    }
}
