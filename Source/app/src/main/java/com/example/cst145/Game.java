package com.example.cst145;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tass CST145
 */
public class Game extends AppCompatActivity
{

    //INTENT EXTRAS
    public static final String STRING_EXTRA_NAME = "name";
    public static final String STRING_EXTRA_DIFFICULTY = "difficulty";
    public static final String STRING_EXTRA_SCORE = "score";
    private static final double CLOCK_SPEED = 1.0; //change this to adjust game speed
    //GAME LOGIC (could be fun to turn these into extra ints in the intent to create a settings menu
    private static final int ENEMY_SIZE = 50;
    private static final int MAX_ENEMIES = 20;
    private static final int ENEMY_SPAWN_DELAY = (int) (3000 / CLOCK_SPEED);
    private static final int ENEMY_CLOCK_DELAY = (int) (25 / CLOCK_SPEED);
    //CONTROLLER LOGIC
    private static final double DEAD_ZONE = 200.0;
    private static final double PLAYER_SPEED = 10.0 * CLOCK_SPEED;
    private static final float MEDIUM_CONTROLLER_MOVEMENT_FACTOR = 1.5F;
    private static final int TOUCH_DELAY = 30;
    //SCREEN DIMENSIONS
    private static final int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    private static final int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    private static final int RIGHT_BORDER = WIDTH - 100;
    private static final int BOTTOM_BORDER = HEIGHT - 300;
    //ANIMATION PARAMETERS
    private static final int RED_PLAYER_SIZE = 133;
    private static final int EXPLOSION_SIZE = 250;
    private static final int RED_STOP = 50;
    private static final int FULL_HUE = 255;
    //THE LITERAL MOST POWERFUL THREADING TOOL IVE EVER SEEN
    private static final Handler loopHandler = new Handler(Looper.myLooper());


    private final List<Integer> drawableID = Arrays.asList(R.drawable.bang1, R.drawable.bang2, R.drawable.bang3,
            R.drawable.bang4, R.drawable.bang5, R.drawable.bang6, R.drawable.bang7, R.drawable.bang8, R.drawable.bang9, R.drawable.bang10, R.drawable.bang11, R.drawable.bang12);
    private final ArrayList<Drawable> images = new ArrayList<>();

    private final int[] animDelay = {3, 0, 2000, 100, 100};
    private View home;
    private View me;
    private View gamer;
    private ArrayList<Enemy> enemyList;
    private TextView txtScore;
    private LayoutInflater inflater;
    private int score = 0;
    private String playerName;
    private View line;
    private ImageView btnController;
    private View frame;
    private int speed = 5 * (int) CLOCK_SPEED;
    private boolean alive = true;
    private float tempX = 0;
    private float tempY = 0;
    private int difficulty;
    private Intent endGame;
    private int animScene;
    private int animCounter;
    private float scoreSpeed;

    /**
     * onCreate method. preps game for the user to play
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        prepTheMadness();
    }

    /**
     * back button override to prevent users accidentally backing out of the game
     * I thought about making a pause button, but if i spend any more time on this assignment my CWEB195 group will kill me
     */
    @Override
    public void onBackPressed()
    {

    }

    /**
     * Method used to prepare and launch the game
     * inflates drawables and starts loops
     */
    @SuppressLint("ClickableViewAccessibility")
    void prepTheMadness()
    {
        animScene = 0;
        animCounter = 255;
        btnController = findViewById(R.id.ivController);

        Intent parentIntent = this.getIntent();
        playerName = parentIntent.getStringExtra(STRING_EXTRA_NAME);
        difficulty = parentIntent.getIntExtra(STRING_EXTRA_DIFFICULTY, 1);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        home = findViewById(R.id.btnHome);
        me = findViewById(R.id.btnMe);
        gamer = findViewById(R.id.btnGamer);

        line = findViewById(R.id.btnLine);
        txtScore = findViewById(R.id.txtCurrentScore);
        enemyList = new ArrayList<>();
        frame = findViewById(R.id.frameLayout);

        drawableID.forEach(x -> images.add(AppCompatResources.getDrawable(this, x)));

        btnController.setOnTouchListener((view, event) -> {
            controllerTouchHandler(event);
            return true;
        });

        endGame = new Intent(this, EndGame.class);
        endGame.putExtra(STRING_EXTRA_NAME, playerName);
        loopHandler.post(enemySpawner);
        loopHandler.post(enemyCycle);
    }

    /**
     * Method used to draw a rectangle around player and enemy views. returns true if rectangles collide
     *
     * @param ibPlayer
     * @param enemyView
     * @return
     */
    private boolean getCollision(View ibPlayer, View enemyView)
    {
        float playerX = ibPlayer.getX();
        float playerY = ibPlayer.getY();
        float enemyX = enemyView.getX();
        float enemyY = enemyView.getY();

        Rect enemyRect = new Rect(
                (int) enemyX,
                (int) enemyY,
                (int) enemyX + enemyView.getWidth(),
                (int) enemyY + enemyView.getHeight());
        Rect playerRect = new Rect(
                (int) playerX,
                (int) playerY,
                (int) playerX + ibPlayer.getWidth(),
                (int) playerY + ibPlayer.getHeight());

        return enemyRect.intersect(playerRect);

    }

    /**
     * I think i like math now. Am i becoming one of the uni eggheads?
     *
     * @param homePos
     * @param fingerPos
     * @return
     */
    public double[] eggheadMathematics(View homePos, View fingerPos)
    {

        float homeX = homePos.getX() + getCenterX(home);//get initial touch location
        float homeY = homePos.getY() + getCenterY(home);
        float fingerX = fingerPos.getX() + getCenterX(fingerPos);//get current touch location
        float fingerY = fingerPos.getY() + getCenterX(fingerPos);

        if(fingerY == homeY && fingerX == homeX)
        {
            return new double[]{0, 0}; //check to see if the rest of the method is even necessary
        }

        double baseLength = fingerX - homeX;
        double heightLength = fingerY - homeY;

        double pythagoreansLegacy = Math.sqrt(baseLength * baseLength + heightLength * heightLength);
        //if only my grade 7 teacher could see me now

        runOnUiThread(() -> {
            line.setLayoutParams(new FrameLayout.LayoutParams((int) pythagoreansLegacy, (int) Math.min(PLAYER_SPEED, (int) (pythagoreansLegacy * PLAYER_SPEED) / (DEAD_ZONE))));
            line.setRotation((float) Math.toDegrees(Math.atan(heightLength / baseLength)));
            line.setX(((homeX + fingerX) / 2) - (getCenterX(line)));
            line.setY(((homeY + fingerY) / 2) - (getCenterY(line)));
        });
        // i had to relearn trigonometry to for this section

        if(pythagoreansLegacy < DEAD_ZONE)
        {
            return new double[]{baseLength / (DEAD_ZONE / PLAYER_SPEED), heightLength / (DEAD_ZONE / PLAYER_SPEED)};
        }
        else
        {
            double ratioDelta = (PLAYER_SPEED) / pythagoreansLegacy;
            return new double[]{baseLength * ratioDelta, heightLength * ratioDelta};
        }

    }

    /**
     * method called when player dies. pauses stuff and then triggers death animations
     */
    @SuppressLint("ClickableViewAccessibility")
    private void kaboom()
    {
        alive = false;
        final int finalScore = score;
        btnController.setOnTouchListener(null);
        controllerUp();
        loopHandler.removeCallbacks(enemySpawner);
        loopHandler.removeCallbacks(enemyCycle);
        runOnUiThread(() -> frame.setBackgroundColor(Color.rgb(FULL_HUE, FULL_HUE, FULL_HUE)));
        endGame.putExtra(Game.STRING_EXTRA_SCORE, finalScore);
        scoreSpeed = (float) score / (FULL_HUE - RED_STOP);
        loopHandler.post(dead);


    }

    /**
     * method used to finalize player death animation. offers a button to take player to end screen
     */
    void escapeVector()
    {
        loopHandler.removeCallbacks(dead);
        Button btnContinue = findViewById(R.id.btnYeety);
        btnContinue.setVisibility(View.VISIBLE);
        btnContinue.setOnClickListener(e -> this.startActivity(endGame));
    }

    /**
     * touch handler of large invisible imagebutton, which acts at the controller
     * gets touch location and type, acts accordingly
     *
     * @param event
     */
    void controllerTouchHandler(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                controllerTouchDown(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                controllerMove(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                controllerUp();
                break;
        }

    }

    /**
     * helper method to get the center X of a view.
     *
     * @param v
     * @return
     */
    float getCenterX(View v)
    {
        return v.getWidth() / 2F;
    }

    /**
     * helper method to get the center Y of a view
     *
     * @param v
     * @return
     */
    float getCenterY(View v)
    {
        return v.getHeight() / 2F;
    }

    /**
     * event handler for when a touch occurs. only applies to medium and hard difficulty
     * makes touch points visible and triggers controller loops, if needed
     *
     * @param touchX
     * @param touchY
     */
    void controllerTouchDown(float touchX, float touchY)
    {

        if(difficulty == 1)
        {
            return;
        }
        home.setX(touchX - getCenterX(home));
        home.setY(touchY - getCenterY(home));
        me.setX(home.getX());
        me.setY(home.getY());
        loopHandler.postDelayed(() -> {
            me.setVisibility(View.VISIBLE);
            if(difficulty == 3)
            {

                home.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                loopHandler.postDelayed(controllerHard, TOUCH_DELAY);
            }
            if(difficulty == 2)
            {
                loopHandler.postDelayed(controllerMedium, TOUCH_DELAY);
            }
        }, 10);

    }

    /**
     * controller move handler. if easy, sets player to touch location
     * if medium or hard, stores touch location for later reference
     *
     * @param touchX
     * @param touchY
     */
    void controllerMove(float touchX, float touchY)
    {
        if(difficulty == 1)
        {
            gamer.setX(touchX - getCenterX(gamer));
            gamer.setY(touchY - getCenterY(gamer));
        }
        else
        {
            tempX = touchX - getCenterX(home);
            tempY = touchY - getCenterY(home);
        }
    }

    /**
     * controller touch remove handler. makes controller-specific items invisible
     */
    void controllerUp()
    {
        me.setVisibility(View.INVISIBLE);
        home.setVisibility(View.INVISIBLE);
        line.setVisibility(View.INVISIBLE);
    }

    /**
     * method used to the player, so long as doing so does not cause a collision with a screen edge
     *
     * @param newPosX
     * @param newPosY
     */
    void attemptMove(double newPosX, double newPosY)
    {

        if(newPosX > 0 && newPosX < RIGHT_BORDER)
        {
            gamer.setX((int) newPosX);
        }
        if(newPosY > 0 && newPosY < BOTTOM_BORDER)
        {
            gamer.setY((int) newPosY);
        }
    }

    /**
     * runnable to be looped when controller is used on medium difficulty.
     * controls player around at an offset and at a slight delay compared to the touch input
     * loops as needed
     */
    Runnable controllerMedium = new Runnable()
    {
        @Override
        public void run()
        {

            me.setX(tempX);
            me.setY(tempY);
            double newPosX = ((me.getX() - home.getX()) / MEDIUM_CONTROLLER_MOVEMENT_FACTOR + gamer.getX());
            double newPosY = ((me.getY() - home.getY()) / MEDIUM_CONTROLLER_MOVEMENT_FACTOR + gamer.getY());
            attemptMove(newPosX, newPosY);
            home.setX(tempX);
            home.setY(tempY);


            if(me.getVisibility() == View.VISIBLE)
            {
                loopHandler.postDelayed(this, ENEMY_CLOCK_DELAY);
            }
        }
    };

    /**
     * runnable used in the controller on hard mode. calculates distance between current touch location
     * and initial touch. does a bunch of dumb math to move the player accordingly
     */
    Runnable controllerHard = new Runnable()
    {
        @Override
        public void run()
        {
            me.setX(tempX);
            me.setY(tempY);
            double[] movement = eggheadMathematics(home, me);
            double newPosX = movement[0] + gamer.getX();
            double newPosY = movement[1] + gamer.getY();
            attemptMove(newPosX, newPosY);
            if(me.getVisibility() == View.VISIBLE)
            {
                loopHandler.postDelayed(this, ENEMY_CLOCK_DELAY);
            }
        }

    };

    /**
     * runnable used to spawn new enemies. happens more frequently the higher your difficulty is
     */
    Runnable enemySpawner = new Runnable()
    {
        @Override
        public void run()
        {
            View ve = inflater.inflate(R.layout.enemy, findViewById(androidx.appcompat.R.id.content));
            runOnUiThread(() -> addContentView(ve, new FrameLayout.LayoutParams(ENEMY_SIZE, ENEMY_SIZE)));
            Enemy obEnemy = new Enemy(ve, WIDTH, HEIGHT, speed++);
            enemyList.add(obEnemy);
            if(alive && enemyList.size() < MAX_ENEMIES)
            {
                loopHandler.postDelayed(this, ENEMY_SPAWN_DELAY / difficulty);
            }
        }
    };

    /**
     * Runnable used in the cycle of one enemy. moves enemies, checks for collisions, and loops
     */
    Runnable enemyCycle = new Runnable()
    {
        @Override
        public void run()
        {

            txtScore.setText(String.format(getResources().getString(R.string.score_counter), playerName, score += difficulty));
            for(Enemy obEnemy : enemyList)
            {
                obEnemy.moveIt();
                if(getCollision(gamer, obEnemy.enemyView))
                {
                    kaboom();
                }
            }
            if(alive)
            {
                loopHandler.postDelayed(this, ENEMY_CLOCK_DELAY);
            }
        }
    };

    /**
     * runnable called upon player death. triggers a neat animation i built using the switch below.
     */
    Runnable dead = new Runnable()
    {
        @Override
        public void run()
        {
            boolean yeet = false;
            switch(animScene)
            {
                case 0:

                    runOnUiThread(() -> {
                        frame.setBackgroundColor(Color.rgb(255, animCounter, animCounter--));
                        txtScore.setText(String.format(getResources().getString(R.string.score_counter), playerName, score -= (score - scoreSpeed) > 0 ? scoreSpeed : 0));
                    });
                    animScene += animCounter == RED_STOP ? 1 : 0;
                    break;
                case 1:
                    animCounter = 0;
                    View ve = inflater.inflate(R.layout.enemy, findViewById(androidx.appcompat.R.id.content));
                    runOnUiThread(() -> {
                        addContentView(ve, new FrameLayout.LayoutParams(RED_PLAYER_SIZE, RED_PLAYER_SIZE));
                        txtScore.setText(String.format(getResources().getString(R.string.score_counter), playerName, 0));
                    });
                    Enemy obEnemy = new Enemy(ve, WIDTH, HEIGHT, 0);
                    obEnemy.enemyView.setX(gamer.getX());
                    obEnemy.enemyView.setY(gamer.getY());
                    gamer.setVisibility(View.GONE);
                    enemyList.add(obEnemy);

                    animScene++;
                    break;
                case 2:
                    for(Enemy enemy : enemyList)
                    {
                        enemy.enemyView.setVisibility(View.INVISIBLE);
                        enemy.enemyView.setLayoutParams(new FrameLayout.LayoutParams(EXPLOSION_SIZE, EXPLOSION_SIZE));
                    }
                    animScene++;
                    break;
                case 3:

                    for(Enemy enemy : enemyList)
                    {
                        enemy.enemyView.setVisibility(View.VISIBLE);
                        enemy.enemyView.setBackground(images.get(animCounter));
                    }
                    animCounter++;
                    yeet = animCounter == images.size() - 1;
                    loopHandler.postDelayed(this, animDelay[animScene]);
                    break;
            }
            if(yeet)
            {
                escapeVector();
            }
            else
            {
                loopHandler.postDelayed(this, animDelay[animScene]);
            }
        }
    };

}