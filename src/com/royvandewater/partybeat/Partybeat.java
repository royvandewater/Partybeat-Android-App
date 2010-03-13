/**
 * 
 */
package com.royvandewater.partybeat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import android.view.View;
import android.widget.TextView;

/**
 * @author "Roy van de Water"
 *
 */
public class Partybeat
{
    private String partyfix = "http://10.0.2.2:8000/";
    public TextView flash;
    LinkedBlockingQueue<URL> command_queue;
    Thread daemon;

    public Partybeat(String partyfix, TextView flash)
    {
        this.partyfix = partyfix;
        this.flash = flash;
        this.command_queue = new LinkedBlockingQueue<URL>();

        this.daemon = new Thread(new Runnable() {

            @Override
            public void run()
            {
                daemon_runtime();
            }
        });
        daemon.start();
    }

    public void play()
    {
        send_action("player/action/play/");
    }

    public void pause()
    {
        send_action("player/action/pause/");
    }

    public void send_action(String action)
    {
        try {
            URL url = new URL(partyfix + action);
            command_queue.add(url);
        } catch (MalformedURLException e) {
            flash.setVisibility(View.VISIBLE);
            flash.setText(e.toString());
        }
    }

    public void daemon_runtime()
    {
        while (true) {
            try {
                URL url = command_queue.take();
                url.openStream();
            } catch (InterruptedException e){
                flash.setVisibility(View.VISIBLE);
                flash.setText(e.toString());
            } catch (IOException e) {
                flash.setVisibility(View.VISIBLE);
                flash.setText(e.toString());
            }
        }
    }

    public void setPartyfix(String partyfix)
    {
        this.partyfix = partyfix;
    }
}
