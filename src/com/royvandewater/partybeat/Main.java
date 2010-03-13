package com.royvandewater.partybeat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity
{
    // Menu constants
    final int MENU_SETTINGS = 0;

    /* partybeat + prefix = partyfix
       the android sdk maps host machine to 10.0.2.2 in emulator since
       127.0.0.1 refers to the virtualized android device itself */
    public static String partyfix;
    public static TextView flash;
    public static Partybeat partybeat;

    // Preferences
    public static final String HOSTNAME = "hostname";
    public static final String PREFERENCESNAME = "AndParty";
    private SharedPreferences preferences = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        flash = (TextView)findViewById(R.id.Flash_text);

        preferences = this.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
        partyfix = preferences.getString(HOSTNAME, "http://10.0.2.2:8000/");
        partybeat = new Partybeat(partyfix, flash);

        Button button_play = (Button)findViewById(R.id.Button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                partybeat.play();
            }
        });

        Button button_pause = (Button)findViewById(R.id.Button_pause);
        button_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                partybeat.pause();
            }
        });
        
    }
    @Override
    protected void onRestart()
    {
        // reload the preferences
        partyfix = preferences.getString(HOSTNAME, "http://10.0.2.2:8000/");
        super.onRestart();
        flash.setVisibility(View.GONE);
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, MENU_SETTINGS, 0, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case MENU_SETTINGS:
                // start setting activity
                try {
                    Intent intent = new Intent(this, Settings.class);
                    startActivity(intent);
                } catch (Exception e) {
                    flash.setVisibility(1);
                    flash.setText(e.toString());
                }
                return true;
        }
        return false;
    }

}
