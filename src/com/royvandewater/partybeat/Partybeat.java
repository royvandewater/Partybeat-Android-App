/**
 * 
 */
package com.royvandewater.partybeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import org.json.JSONException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * @author "Roy van de Water"
 *
 */
public class Partybeat
{
    private String partyfix = "http://10.0.2.2:8000/";
    private LinkedBlockingQueue<URL> command_queue;
    private XmmsStatus status;

    private Thread action_thread, info_thread;
    private Handler gui_thread;

    public static final int STOPPED = 0;
    public static final int PLAYING = 1;
    public static final int PAUSED = 2;

    public Partybeat(String partyfix, Handler gui_thread)
    {
        this.partyfix = partyfix;
        this.gui_thread = gui_thread;
        this.command_queue = new LinkedBlockingQueue<URL>();

        this.action_thread = new Thread(new Runnable() {

            @Override
            public void run()
            {
                action_runtime();
            }
        });
        action_thread.start();

        this.info_thread = new Thread(new Runnable() {

            @Override
            public void run()
            {
                info_runtime();
            }
        });
        info_thread.start();
    }

    public void play(View v)
    {
        send_action("player/action/play/");
    }

    public void pause(View v)
    {
        send_action("player/action/pause/");
    }

    public void next(View v)
    {
        send_action("player/action/next/");
    }

    public void previous(View v)
    {
        send_action("player/action/previous/");
    }

    public void send_action(String action)
    {
        try {
            URL url = new URL(partyfix + action);
            command_queue.add(url);
        } catch (MalformedURLException e) {
            send_error("Malformed url, please check your settings");
        }
    }

    public void action_runtime()
    {
        while (true) {
            try {
                URL url = command_queue.take();
                url.openStream();
            } catch (InterruptedException e) {
                send_error(e.getMessage());
            } catch (IOException e) {
                send_error(e.getMessage());
            }
        }
    }

    public void info_runtime()
    {

        try {
            while (true) {
                URL url = new URL(partyfix + "player/info/");
                InputStream stream = url.openStream();
                String current_info = convertStreamToString(stream);
                this.status = new XmmsStatus(current_info);
                send_status(this.status);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    send_error("My nap was interrupted!");
                }
            }
        } catch (MalformedURLException e) {
            // Do nothing as the user has already been notified
        } catch (IOException e) {
            send_error("Problem parsing partybeat data");
        } catch (JSONException e) {
            send_error("Malformed JSON response from server");
        }
    }

    public void setPartyfix(String partyfix)
    {
        this.partyfix = partyfix;
    }

    public void send_error(String error_message)
    {
        Bundle data = new Bundle();
        data.putString("error", error_message);
        Message error = Message.obtain();
        error.setData(data);
        gui_thread.sendMessage(error);
    }

    public void send_status(XmmsStatus status)
    {
        Bundle data = new Bundle();
        data.putInt("status", status.getCurrent_action());
        Message message = Message.obtain();
        message.setData(data);
        gui_thread.sendMessage(message);
    }

    public static String convertStreamToString(InputStream is) throws IOException
    {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
