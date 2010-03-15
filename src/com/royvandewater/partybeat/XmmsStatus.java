package com.royvandewater.partybeat;

import org.json.JSONException;
import org.json.JSONObject;

public class XmmsStatus
{
    private String hash;
    private boolean playing;
    private int current_action;
    private int seek;
    private int max_seek;
    private int volume;

    public XmmsStatus(String json_string) throws JSONException
    {
        JSONObject status = new JSONObject(json_string).getJSONObject("xmms2").getJSONObject("player_status");
        this.hash = status.getString("hash");
        this.playing = status.getBoolean("is_playing");
        this.current_action = status.getInt("current_action");
        this.seek = status.getInt("seek");
        this.max_seek = status.getInt("max_seek");
        this.volume = status.getInt("volume");
    }

    public String getHash()
    {
        return hash;
    }

    public boolean is_playing()
    {
        return playing;
    }

    public int getCurrent_action()
    {
        return current_action;
    }

    public int getSeek()
    {
        return seek;
    }

    public int getMax_seek()
    {
        return max_seek;
    }

    public int getVolume()
    {
        return volume;
    }

}
