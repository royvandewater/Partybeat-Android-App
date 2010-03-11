/**
 * 
 */
package com.royvandewater.partybeat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author "Roy van de Water"
 *
 */
public class Settings extends Activity
{
    public static final String HOSTNAME = "hostname";
    public static final String PREFERENCESNAME = "AndParty";

    private SharedPreferences preferences = null;
    private Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        final EditText hostname_edit = (EditText)findViewById(R.id.hostname);
        final Button save_button = (Button)findViewById(R.id.save_button);

        preferences = this.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        hostname_edit.setText(preferences.getString(HOSTNAME, "http://10.0.2.2:8000/"));

        // Persistant preferences object

        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                try {
                    String hostname = hostname_edit.getText().toString();
                    if (hostname.trim().length() == 0) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                        AlertDialog ad = adb.create();
                        ad.setMessage("Hostname is required");
                        ad.show();
                        return;
                    }
                    if (!hostname.endsWith("/"))
                        hostname += "/";
                    // Store the data
                    editor.putString(Settings.HOSTNAME, hostname);
                    editor.commit();
                    finish();
                } catch (Exception e) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                    AlertDialog ad = adb.create();
                    ad.setMessage("An error occured [" + e.getMessage() + "]");
                    ad.show();
                    return;
                }
            }
        });
    }

}
