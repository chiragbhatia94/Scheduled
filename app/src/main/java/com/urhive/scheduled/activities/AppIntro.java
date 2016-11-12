package com.urhive.scheduled.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.urhive.scheduled.R;
import com.urhive.scheduled.models.Category;
import com.urhive.scheduled.models.Icon;
import com.urhive.scheduled.models.Quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class AppIntro extends IntroActivity {
    SharedPreferences prefs;
    AsyncTask x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // run the app intro activity here
        final Runnable initialisingDB = new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("pref_first_run", false);
                editor.apply();

                String icons[] = AppIntro.this.getResources().getStringArray(R.array.icons_string_array);

                for (int i = 0; i < icons.length; i++) {
                    Icon icon = new Icon(icons[i], 0);
                    icon.save();
                    // System.out.println("Icon saved! " + icon.getId());
                }

                List<Icon> iconsU = Icon.find(Icon.class, "name = ?", "ic_label_white_24dp");
                Category uncategorized = new Category(getString(R.string.category_uncategorized), 12, Integer.parseInt(String.valueOf(iconsU.get(0).getId())), 0, 0);
                uncategorized.save();

                try {
                    quotesFromFile(AppIntro.this, R.raw.quotes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        x = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                initialisingDB.run();
                return null;
            }
        };
        x.execute();
    }

    /**
     * This reads a file from the given Resource-Id and calls every line of it as a SQL-Statement
     *
     * @param context
     * @param resourceId e.g. R.raw.food_db
     * @return Number of SQL-Statements run
     * @throws IOException
     */
    public int quotesFromFile(Context context, int resourceId) throws IOException {
        // Reseting Counter
        int result = 0;

        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        // Iterate through lines (assuming each insert has its own line and theres no other stuff)
        while (insertReader.ready()) {
            String insertStmt = insertReader.readLine();
            /*db.execSQL(insertStmt);*/
            Quotes.executeQuery(insertStmt);
            result++;
        }
        insertReader.close();

        // returning number of inserted rows
        return result;
    }

    public void next(View view) {
        if (x.getStatus() == AsyncTask.Status.RUNNING) {
            Toast.makeText(AppIntro.this, "Wait", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(AppIntro.this, "Completed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
