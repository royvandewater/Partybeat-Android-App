package com.royvandewater.partybeat;

import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
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
    public static Handler gui_thread;

    // Preferences
    public static final String HOSTNAME = "hostname";
    public static final String PREFERENCESNAME = "AndParty";
    private SharedPreferences preferences = null;

    // Constants for message types
    public enum MessageType {
        ERROR, STATUS, NOVALUE;

        public static MessageType toMessageType(String str)
        {
            try {
                return valueOf(str.toUpperCase());
            } catch (Exception e) {
                return NOVALUE;
            }

        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Load in the flash object for error message display
        flash = (TextView)findViewById(R.id.Flash_text);

        // Instantiate a handler for the current Thread
        gui_thread = new Handler(new Callback() {

            @Override
            public boolean handleMessage(Message message)
            {
                Bundle data = message.getData();
                Set<String> keyset = data.keySet();

                for (String key : keyset) {
                    switch (MessageType.toMessageType(key)) {
                        case ERROR:
                            String error_message = data.getString(key);
                            show_alert(error_message);
                            break;
                        case STATUS:
                            int status = data.getInt("status");
                            set_status(status);
                            break;
                        default:
                            break;
                    }
                }

                return false;
            }
        });

        // Load preferences from user configuration
        preferences = this.getSharedPreferences(PREFERENCESNAME, Context.MODE_PRIVATE);
        partyfix = preferences.getString(HOSTNAME, "http://10.0.2.2:8000/");

        partybeat = new Partybeat(partyfix, gui_thread);

        Button button_play = (Button)findViewById(R.id.Button_play);
        button_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                partybeat.play(v);
            }
        });

        Button button_pause = (Button)findViewById(R.id.Button_pause);
        button_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                partybeat.pause(v);
            }
        });

        Button button_next = (Button)findViewById(R.id.Button_next);
        button_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                partybeat.next(v);
            }
        });

        Button button_previous = (Button)findViewById(R.id.Button_previous);
        button_previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                partybeat.previous(v);
            }
        });

    }

    private void set_status(int status)
    {
        Button button_play = (Button)findViewById(R.id.Button_play);
        Button button_pause = (Button)findViewById(R.id.Button_pause);

        switch (status) {
            case 0: //Stopped
            case 2: //Paused
                button_pause.setVisibility(View.GONE);
                button_play.setVisibility(View.VISIBLE);
                return;
            case 1: //Playing
                button_pause.setVisibility(View.VISIBLE);
                button_play.setVisibility(View.GONE);
                return;
            default:
                return;
        }
    }

    @Override
    protected void onRestart()
    {
        // reload the preferences
        partyfix = preferences.getString(HOSTNAME, "http://10.0.2.2:8000/");
        super.onRestart();
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
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void show_alert(String message)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        AlertDialog ad = adb.create();
        ad.setMessage(message);
        ad.setButton(Dialog.BUTTON_POSITIVE, "Ok", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        ad.show();
    }
}
