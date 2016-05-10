package net.crevion.singgih.popularmoviesapp.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import net.crevion.singgih.popularmoviesapp.main.MainActivityFragment;


/**
 * Created by singgih on 06/05/2016.
 */
public class Dialog extends AppCompatActivity {

    public AlertDialog.Builder buildDialogSetting(Context c) {
        final Context context = c;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("Seems your internet settings is wrong. Go to settings menu?");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder;
    }



}
