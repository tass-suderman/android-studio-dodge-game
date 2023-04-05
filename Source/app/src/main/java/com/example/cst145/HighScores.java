package com.example.cst145;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * @author Tass CST145
 */
public class HighScores extends AppCompatActivity
{

    private static final int[] lstID = {
            R.id.txtScore1N, R.id.txtScore1S, R.id.txtScore1T, R.id.txtScore1La, R.id.txtScore1Lo,
            R.id.txtScore2N, R.id.txtScore2S, R.id.txtScore2T, R.id.txtScore2La, R.id.txtScore2Lo,
            R.id.txtScore3N, R.id.txtScore3S, R.id.txtScore3T, R.id.txtScore3La, R.id.txtScore3Lo,
            R.id.txtScore4N, R.id.txtScore4S, R.id.txtScore4T, R.id.txtScore4La, R.id.txtScore4Lo,
            R.id.txtScore5N, R.id.txtScore5S, R.id.txtScore5T, R.id.txtScore5La, R.id.txtScore5Lo,
            R.id.txtScore6N, R.id.txtScore6S, R.id.txtScore6T, R.id.txtScore6La, R.id.txtScore6Lo,
            R.id.txtScore7N, R.id.txtScore7S, R.id.txtScore7T, R.id.txtScore7La, R.id.txtScore7Lo,
            R.id.txtScore8N, R.id.txtScore8S, R.id.txtScore8T, R.id.txtScore8La, R.id.txtScore8Lo,
            R.id.txtScore9N, R.id.txtScore9S, R.id.txtScore9T, R.id.txtScore9La, R.id.txtScore9Lo,
            R.id.txtScore10N, R.id.txtScore10S, R.id.txtScore10T, R.id.txtScore10La, R.id.txtScore10Lo
    };

    private static final String CONFIRM_DELETE = "Are you sure you want to clear the scores?";
    private static final String CONFIRM_YES = "Yes, delete the scores.";
    private static final String CONFIRM_NO = "No, cancel.";

    ScoreDBHelper dbScore;

    ArrayList<ArrayList<TextView>> scoreListHell = new ArrayList<>();

    /**
     * onCreate method for high score screen.
     * it initializes and populates the score screen with scoring info
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        Button btnBack = findViewById(R.id.btnScoreBack);
        btnBack.setOnClickListener(e -> btnBackClick());

        Button btnDeleteDB = findViewById(R.id.btnClearDB);
        btnDeleteDB.setOnClickListener(e -> btnDeleteClick());

        dbScore = new ScoreDBHelper(this);

        ArrayList<Score> scores = dbScore.getAllScores();

        int index = 0;

        for(int i = 0; i < 10; i++)
        {
            if(scores.size() <= i)
            {
                break;
            }
            ArrayList<String> lstScoreData = scores.get(i).getFields();
            scoreListHell.add(new ArrayList<>());
            for(int j = 0; j < 5; j++)
            {
                scoreListHell.get(i).add(findViewById(lstID[index++]));
                scoreListHell.get(i).get(j).setText(lstScoreData.get(j));
            }
        }

    }

    /**
     * code inspired from
     * https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
     * Acts as a confirmation popup to confirm clearing the scoring information
     */
    private void btnDeleteClick()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(CONFIRM_DELETE)
                .setPositiveButton(CONFIRM_YES, (x, y) -> {
                    dbScore.deleteScores();
                    Intent backToHome = new Intent(this, MainActivity.class);
                    this.startActivity(backToHome);
                }).setNegativeButton(CONFIRM_NO, (x, y) -> {
                }).show();

    }


    /**
     * Back button override: now acts as a means to take the player to the homepage
     */
    @Override
    public void onBackPressed()
    {
        btnBackClick();
    }

    /**
     * Event handler for the onscreen back button: returns user to the homepage
     */
    private void btnBackClick()
    {
        Intent home = new Intent(this, MainActivity.class);
        this.startActivity(home);
    }
}

