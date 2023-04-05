package com.example.cst145;

import android.location.Location;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author Tass CST145
 */
public class Score
{

    public long id;
    public String name;
    public int score;
    public String time;
    public double latitude;
    public double longitude;

    /**
     * Score constructor. Adds player name, score, and location info
     *
     * @param sName
     * @param nScore
     * @param obLob
     */
    public Score(String sName, int nScore, Location obLob)
    {
        this.id = -1;
        this.name = sName;
        this.score = nScore;

        if(obLob == null)
        {
            this.time = DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
            this.latitude = 0;
            this.longitude = 0;
        }
        else
        {
            this.time = DateFormat.getDateTimeInstance().format(obLob.getTime());
            this.latitude = obLob.getLatitude();
            this.longitude = obLob.getLongitude();
        }
    }

    /**
     * Score constructor, called from the Database and used to populate score list
     *
     * @param id
     * @param sName
     * @param nScore
     * @param sTime
     * @param dLat
     * @param dLon
     */
    public Score(int id, String sName, int nScore, String sTime, double dLat, double dLon)
    {
        this.id = id;
        this.name = sName;
        this.score = nScore;
        this.time = sTime;
        this.latitude = dLat;
        this.longitude = dLon;
    }


    /**
     * returns score object info in an easily displayable manner for the scoring list
     *
     * @return
     */
    public ArrayList<String> getFields()
    {
        return new ArrayList<>(Arrays.asList(
                String.format(Locale.CANADA,
                                "%s;%d;%s;%f;%f",
                                name, score, time, latitude, longitude)
                        .split(";")));
    }

}
