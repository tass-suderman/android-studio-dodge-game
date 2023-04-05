package com.example.cst145;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Tass CST145
 */
public class EndGame extends AppCompatActivity
{

    String name;
    int points;

    /**
     * onCreate method, called when the game ends
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        loadResources();

        addScoreToDB();
    }


    /**
     * I set the back button to take you back to the homepage when in this activity
     */
    @Override
    public void onBackPressed()
    {
        btnHomeClick();
    }


    /**
     * this activity initializes and populates data. it is also used to set the onclick listeners
     */
    private void loadResources()
    {

        Resources res = getResources();
        TextView tvGG = findViewById(R.id.tvGG);
        TextView tvScore = findViewById(R.id.tvPoints);

        Intent parentIntent = this.getIntent();
        name = parentIntent.getStringExtra(Game.STRING_EXTRA_NAME);
        points = parentIntent.getIntExtra(Game.STRING_EXTRA_SCORE, 0);
        tvGG.setText(String.format(res.getString(R.string.goodgame), name));
        tvScore.setText(String.format(res.getString(R.string.score), points));

        Button btnHighScores = findViewById(R.id.btnEndScore);
        Button btnBackToMenu = findViewById(R.id.btnEndHome);

        btnBackToMenu.setOnClickListener(e -> btnHomeClick());
        btnHighScores.setOnClickListener(e -> btnScoreClick());

    }

    /**
     * This method is called when the home button is clicked. it takes you back to the home page
     */
    private void btnHomeClick()
    {

        Intent backToHome = new Intent(this, MainActivity.class);
        this.startActivity(backToHome);
    }

    /**
     * This method is called when the score button is clicked, and it redirects the user to the highscore page
     */
    private void btnScoreClick()
    {

        Intent highScoresEnd = new Intent(this, HighScores.class);
        this.startActivity(highScoresEnd);
    }


    /**
     * This method is called to create a score and add it to the database.
     */
    private void addScoreToDB()
    {

        ScoreDBHelper dbScore = new ScoreDBHelper(this);

        Location obLoc = MainActivity.obLoc;

        Score obScore = new Score(name, points, obLoc);
        try
        {

            dbScore.createScore(obScore);

        }
        catch(SQLiteException e)
        {
            dbScore.onCreate(dbScore.sqlDB);
        }
    }

}