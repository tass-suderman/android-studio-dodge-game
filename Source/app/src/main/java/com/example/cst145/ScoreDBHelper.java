package com.example.cst145;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author Tass CST145
 */
public class ScoreDBHelper extends SQLiteOpenHelper
{

    public static final String ID = "_id";
    public static final String NAME = "playername";
    public static final String SCORE = "score";
    public static final String TIME = "time";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private static final String DB_NAME = "scores.db";
    private static final String TABLE_NAME = "HighScores";
    private static final int DB_VERSION = 1;

    private static final String TABLECREATE = "CREATE TABLE " + TABLE_NAME + " (";
    private static final String IDREQS = " integer primary key autoincrement, ";
    private static final String NAMEREQS = " text not null, ";
    private static final String SCOREREQS = " integer not null, ";
    private static final String TIMEREQS = " text not null, ";
    private static final String LATREQS = " double not null, ";
    private static final String LONREQS = " double not null);";


    public SQLiteDatabase sqlDB;

    /**
     * constructor for scoredbhelper
     *
     * @param context
     */
    public ScoreDBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * table create method
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String sCreate = TABLECREATE +
                ID + IDREQS +
                NAME + NAMEREQS +
                SCORE + SCOREREQS +
                TIME + TIMEREQS +
                LATITUDE + LATREQS +
                LONGITUDE + LONREQS;
        sqLiteDatabase.execSQL(sCreate);
    }

    /**
     * table upgrade method. i dont think i ever use it, but android needs it
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * method to add a score. Takes a score object and injects it into the database
     *
     * @param scoreData
     */
    public void createScore(Score scoreData)
    {
        open();
        ContentValues cvs = new ContentValues();
        cvs.put(NAME, scoreData.name);
        cvs.put(SCORE, scoreData.score);
        cvs.put(TIME, scoreData.time);
        cvs.put(LATITUDE, scoreData.latitude);
        cvs.put(LONGITUDE, scoreData.longitude);

        scoreData.id = sqlDB.insert(TABLE_NAME, null, cvs);
    }

    /**
     * method for collecting all scores and returning them in an arraylist
     *
     * @return
     */
    public ArrayList<Score> getAllScores()
    {
        open();
        ArrayList<Score> lstScores = new ArrayList<>();
        Cursor scoreCursor = getAllScoresAsCursor();
        while(scoreCursor.moveToNext())
        {
            int idCol = scoreCursor.getInt(scoreCursor.getColumnIndexOrThrow(ID));
            String nameCol = scoreCursor.getString(scoreCursor.getColumnIndexOrThrow(NAME));
            int scoreCol = scoreCursor.getInt(scoreCursor.getColumnIndexOrThrow(SCORE));
            String timeCol = scoreCursor.getString(scoreCursor.getColumnIndexOrThrow(TIME));
            double latitudeCol = scoreCursor.getDouble(scoreCursor.getColumnIndexOrThrow(LATITUDE));
            double longitudeCol = scoreCursor.getDouble(scoreCursor.getColumnIndexOrThrow(LONGITUDE));
            lstScores.add(new Score(idCol, nameCol, scoreCol, timeCol, latitudeCol, longitudeCol));
        }
        if(lstScores.size() >= 2)
        {
            lstScores = lstScores.stream().sorted((x, y) -> y.score - x.score).limit(10).collect(Collectors.toCollection(ArrayList::new));
        }
        close();
        return lstScores;
    }

    /**
     * method to open database
     *
     * @throws SQLException
     */
    private void open() throws SQLException
    {
        sqlDB = this.getWritableDatabase();

    }

    /**
     * exciting method. closes the database
     */
    public void close()
    {
        sqlDB.close();

    }

    /**
     * method which returns all database scores as a cursor.
     *
     * @return
     */
    private Cursor getAllScoresAsCursor()
    {
        String[] sFields = {ID, NAME, SCORE, TIME, LATITUDE, LONGITUDE};
        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null);
    }

    /**
     * method called to clear the database
     */
    public void deleteScores()
    {
        open();
        sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqlDB);
        close();

    }


}
