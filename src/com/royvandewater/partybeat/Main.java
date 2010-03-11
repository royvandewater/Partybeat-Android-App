package com.royvandewater.partybeat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity
{
    final String partybeat_url = "http://localhost:8000/";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView flash = (TextView)findViewById(R.id.Flash_text);
        
        Button button_play = (Button)findViewById(R.id.Button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                flash.setText("Playing");
            }
        });
        
    }
    
}
