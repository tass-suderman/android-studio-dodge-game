package com.example.cst145;

import android.view.View;

/**
 * @author Tass CST145
 */
public class Enemy
{

    private static final float DISTANCE_FROM_SCREEN = 20;
    public final View enemyView;
    private final float widthBarrier;
    private final float heightBarrier;
    private final int screenWidth;
    private final int screenHeight;
    private final int speed;
    private float directionX;
    private float directionY;

    /**
     * This method is the enemy constructor. it is used to populate an enemy's view, its size and speed
     *
     * @param view
     * @param width
     * @param height
     * @param nSpeed
     */
    public Enemy(View view, int width, int height, int nSpeed)
    {
        this.enemyView = view;
        this.screenWidth = width;
        this.screenHeight = height;
        this.widthBarrier = screenWidth + DISTANCE_FROM_SCREEN;
        this.heightBarrier = screenHeight + DISTANCE_FROM_SCREEN;
        this.speed = nSpeed;
        giveMeDirection();

    }

    /**
     * This method is used to run an enemy's movement for 1 cycle
     * if the enemy reaches the end of the screen, the enemy is placed at a new edge with a new direction
     */
    public void moveIt()
    {
        this.enemyView.setX(this.enemyView.getX() + this.directionX);
        this.enemyView.setY(this.enemyView.getY() + this.directionY);
        if(this.enemyView.getX() < DISTANCE_FROM_SCREEN * -1F || this.enemyView.getX() > widthBarrier || this.enemyView.getY() < DISTANCE_FROM_SCREEN * -1F || this.enemyView.getY() > heightBarrier)
        {
            giveMeDirection();
        }
    }


    /**
     * This method is used to place an enemy at an edge of the screen and give it a direction to travel
     */
    private void giveMeDirection()
    {

        int upDown = Math.random() * 2 > 1 ? 1 : -1;
        boolean spawnHorizontalOrVertical = Math.random() * 2 > 1;

        this.directionX = (float) (Math.random() * speed * 2) - speed;
        this.directionY = (speed - Math.abs(this.directionX)) * upDown;

        float currentX;
        float currentY;

        if(spawnHorizontalOrVertical)
        {
            currentX = this.directionX > 0 ? -DISTANCE_FROM_SCREEN : widthBarrier;
            currentY = (float) (Math.random() * screenHeight);
        }
        else
        {
            currentY = this.directionY > 0 ? -DISTANCE_FROM_SCREEN : heightBarrier;
            currentX = (float) (Math.random() * screenWidth);
        }

        this.enemyView.setX(currentX);
        this.enemyView.setY(currentY);

    }
}
