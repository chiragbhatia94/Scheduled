package com.urhive.scheduled.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by Chirag Bhatia on 13-07-2016.
 */
public class PremiumHelper {
    public static Boolean PREMIUM = Boolean.TRUE;

    public static Boolean checkPremium(final Context context, String title, String msg) {
        if (PREMIUM == true) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "To Be Developed Still", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
            return false;
        }
    }
}
