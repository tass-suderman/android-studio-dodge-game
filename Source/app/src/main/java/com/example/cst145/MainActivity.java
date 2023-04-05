package com.example.cst145;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * @author Tass CST145
 */
public class MainActivity extends AppCompatActivity
{

    public static final String NAME_PATTERN = "[A-Z]{3}";
    public static final String SHARED_PREF_KEY = "player_name";
    public static final String DEFAULT_NAME = "ABC";
    public static final int METERS_TO_UPDATE = 500;
    private static final int MILLIS_IN_SECOND = 1000;
    public static final int NOTIF_TIME = MILLIS_IN_SECOND * 10; //change me to change notif time
    public static final int LOCATION_INTERVAL = MILLIS_IN_SECOND * 10; //change me to change location update frequency
    public static Location obLoc;
    TextView txtTitle;
    TextView txtName;
    TextView txtError;
    EditText etName;

    Button btnStartEasy;
    Button btnStartMed;
    Button btnStartHard;
    Button btnPerms;

    Button btnScore;
    Button btnName;
    Button btnExit;

    LinearLayout startBar;
    LinearLayout permsBar;


    Intent game;


    /**
     * homepage create method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadResources();
    }

    /**
     * overridden back button. now unusable, so people cant bounce in an unpredictable way between activities
     */
    @Override
    public void onBackPressed()
    {

    }

    /**
     * onresume button override. used to update whether to ask for location
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        askForLocation();
    }

    /**
     * method used to populate home page information and event handlers. ends by asking for location, if necessary, and then by adding a location object, if possible
     */
    private void loadResources()
    {
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(R.string.title);
        txtError = findViewById(R.id.txtError);
        txtName = findViewById(R.id.txtName);
        loadName();

        etName = findViewById(R.id.etName);

        btnStartEasy = findViewById(R.id.btnStartEasy);
        btnStartEasy.setOnClickListener(e -> btnStartClickEasy());

        btnStartMed = findViewById(R.id.btnStartMed);
        btnStartMed.setOnClickListener(e -> btnStartClickMedium());

        btnStartHard = findViewById(R.id.btnStartHard);
        btnStartHard.setOnClickListener(e -> btnStartClickHard());

        btnScore = findViewById(R.id.btnScore);
        btnScore.setOnClickListener(e -> btnScoreClick());

        btnName = findViewById(R.id.btnName);
        btnName.setText(R.string.btn_name);
        btnName.setOnClickListener(e -> btnNameClick());

        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(e -> btnExitClick());

        btnPerms = findViewById(R.id.btnPerms);
        btnPerms.setOnClickListener(e -> btnPermsClick());

        startBar = findViewById(R.id.startBar);
        permsBar = findViewById(R.id.permsBar);

        game = new Intent(this, Game.class);


        askForLocation();

        locationCode();

    }

    /**
     * bunch of ugly boilerplate android code. gets location, if possible
     */
    private void locationCode()
    {

        LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        LocationListener locList = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location)
            {
                obLoc = location;
            }

            @Override
            public void onProviderEnabled(@NonNull String provider)
            {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider)
            {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }
        };

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            obLoc = null;
        }
        else
        {
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, METERS_TO_UPDATE, locList);
            obLoc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    /**
     * hitting the exit button on the home page calls this method, it schedules a notification to remind the user to play again.
     * Modern marketing, amirite?
     */
    private void btnExitClick()
    {

        Intent intent = new Intent(this, GameNotification.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + NOTIF_TIME, pendingIntent);
        finishAffinity();
    }

    /**
     * This method is called when you ask to change your name
     * It makes some features unusable until the change is finalized
     */
    private void btnNameClick()
    {
        txtName.setVisibility(View.GONE);
        etName.setVisibility(View.VISIBLE);
        etName.setText(txtName.getText().toString());
        btnName.setText(R.string.btn_name_edit);
        startBar.setVisibility(View.GONE);
        btnScore.setVisibility(View.GONE);
        btnName.setOnClickListener(e -> btnSaveNameClick());
        showKeyboard(etName);
    }

    /**
     * //code inspired by https://stackoverflow.com/questions/1109022/how-to-close-hide-the-android-soft-keyboard-programmatically
     * call up keyboard for name change
     */
    private void showKeyboard(View view)
    {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * hide keyboard on name change
     *
     * @param view
     */
    private void hideKeyboard(View view)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * method called to finalize name change
     */
    private void btnSaveNameClick()
    {
        if(!etName.getText().toString().matches(NAME_PATTERN))
        {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(R.string.error_message);
            return;
        }

        SharedPreferences sharePrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePrefs.edit();
        editor.putString(SHARED_PREF_KEY, etName.getText().toString());
        editor.apply();
        txtError.setText("");
        txtError.setVisibility(View.GONE);
        etName.setVisibility(View.GONE);
        txtName.setVisibility(View.VISIBLE);
        loadName();
        btnName.setText(R.string.btn_name);
        btnName.setOnClickListener(e -> btnNameClick());
        hideKeyboard(etName);
        startBar.setVisibility(View.VISIBLE);
        btnScore.setVisibility(View.VISIBLE);
    }

    /**
     * Method to take user from homepage to score page
     */
    public void btnScoreClick()
    {
        Intent highScoreIntent = new Intent(this, HighScores.class);
        this.startActivity(highScoreIntent);
    }

    /**
     * Method to take user from home page to easy game
     */
    private void btnStartClickEasy()
    {
        game.putExtra(Game.STRING_EXTRA_NAME, txtName.getText().toString());
        game.putExtra(Game.STRING_EXTRA_DIFFICULTY, 1);
        startActivity(game);
    }

    /**
     * Method to take user from home page to medium game
     */
    private void btnStartClickMedium()
    {
        game.putExtra(Game.STRING_EXTRA_NAME, txtName.getText().toString());
        game.putExtra(Game.STRING_EXTRA_DIFFICULTY, 2);
        startActivity(game);
    }

    /**
     * Method to take user from home page to hard game
     */
    private void btnStartClickHard()
    {
        game.putExtra(Game.STRING_EXTRA_NAME, txtName.getText().toString());
        game.putExtra(Game.STRING_EXTRA_DIFFICULTY, 3);
        startActivity(game);
    }

    /**
     * method used to load name from shared preferences
     */
    private void loadName()
    {
        SharedPreferences sharePrefs = getPreferences(MODE_PRIVATE);
        txtName.setText(sharePrefs.getString(SHARED_PREF_KEY, DEFAULT_NAME));
    }


    /**
     * https://stackoverflow.com/questions/32822101/how-can-i-programmatically-open-the-permission-screen-for-a-specific-app-on-andr
     * respectfully borrowed from this site
     * button event handler. takes user to app page to add perms for location, if they want
     */
    private void btnPermsClick()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * method used to add location request bar, if needed.
     */
    private void askForLocation()
    {
        permsBar.setVisibility(View.GONE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            permsBar.setVisibility(View.VISIBLE);
        }
    }
}