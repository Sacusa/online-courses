//
// breakout.c
//
// Computer Science 50
// Problem Set 3
//

// standard libraries
#define _XOPEN_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Stanford Portable Library
#include <spl/gevents.h>
#include <spl/gobjects.h>
#include <spl/gwindow.h>

// height and width of game's window in pixels
#define HEIGHT 600
#define WIDTH 400

// number of rows of bricks
#define ROWS 5

// number of columns of bricks
#define COLS 10

// height and width of bricks in pixels
#define BRICK_HEIGHT 12
#define BRICK_WIDTH 36

// height and width of paddle in pixels
#define PADDLE_HEIGHT 12
#define PADDLE_WIDTH 66

// radius of ball in pixels
#define RADIUS 10

// lives
#define LIVES 3

// prototypes
void initBricks(GWindow window);
GOval initBall(GWindow window);
GRect initPaddle(GWindow window);
GLabel initScoreboard(GWindow window);
void updateScoreboard(GWindow window, GLabel label, int points);
GObject detectCollision(GWindow window, GOval ball);

int main(void)
{
    // seed pseudorandom number generator
    srand48(time(NULL));

    // instantiate window
    GWindow window = newGWindow(WIDTH, HEIGHT);

    // instantiate bricks
    initBricks(window);

    // instantiate ball, centered in middle of window
    GOval ball = initBall(window);

    // instantiate paddle, centered at bottom of window
    GRect paddle = initPaddle(window);

    // instantiate scoreboard, centered in middle of window, just above ball
    GLabel label = initScoreboard(window);

    // number of bricks initially
    int bricks = COLS * ROWS;

    // number of lives initially
    int lives = LIVES;

    // number of points initially
    int points = 0;
    
    // to detect events
    GEvent event;
    
    // store the ball's x and y co-ordinates
    double ball_x = getX(ball);
    double ball_y = getY(ball);
    
    // store the ball's velocity
    double ball_x_velocity = drand48();
    double ball_y_velocity = drand48();
    
    // to start game and check if user wants to exit
    while (true)
    {        
        event = waitForEvent(MOUSE_EVENT);
        
        if (event != NULL)
        {
            if (getEventType(event) == WINDOW_CLOSED)
            {
                // stop and exit game
                closeGWindow(window);
                return 0;
            }
            
            if (getEventType(event) == MOUSE_CLICKED)
            {
                break;
            }
        }
    }

    // keep playing until game over
    while (lives > 0 && bricks > 0)
    {
        // TODO
        ball_x += ball_x_velocity;
        ball_y += ball_y_velocity;
        setLocation(ball, ball_x, ball_y);
        
        if ((ball_x + 20 + ball_x_velocity > 400) || (ball_x + ball_x_velocity < 0))
        {
            ball_x_velocity = -ball_x_velocity;
        }
        
        if (ball_y + ball_y_velocity < 0)
        {
            ball_y_velocity = -ball_y_velocity;
        }
        
        if (ball_y + 20 > 600)
        {
            ball_x = 190;
            ball_y = 322;
            --lives;
            waitForClick();
            setLocation(ball, ball_x, ball_y);
            continue;
        }
        
        GObject object = detectCollision(window, ball);
        
        if (object != NULL)
        {
            if (object == paddle)
            {
                ball_y_velocity = -ball_y_velocity;
            }
            else if ((strcmp(getType(object), "GRect") == 0) && (object != paddle))
            {
                ++points;
                --bricks;
                ball_y_velocity = -ball_y_velocity;
                removeGWindow(window, object);
                updateScoreboard(window, label, points);
                continue;
            }
        }
        
        event = getNextEvent(MOUSE_EVENT);
        
        if (event != NULL)
        {
            if (getEventType(event) == WINDOW_CLOSED)
            {
                // stop and exit game
                closeGWindow(window);
                return 0;
            }
            
            if (getEventType(event) == MOUSE_MOVED)
            {
                double x = getX(event) - (PADDLE_WIDTH / 2);
                
                if (x < 0)
                {
                    setLocation(paddle, 0, 538);    
                }
                else if ((x + PADDLE_WIDTH) > 400)
                {
                    setLocation(paddle, 400 - PADDLE_WIDTH, 538);
                }
                else
                {
                    setLocation(paddle, x, 538);
                }
            }
        }
        
        pause(1);
    }

    // game over
    closeGWindow(window);
    return 0;
}

/**
 * Initializes window with a grid of bricks.
 */
void initBricks(GWindow window)
{
    // TODO
    int i = 0, j = 0;
    
    for(i = 0; i < 5; ++i)
    {
        for(j = 0; j < 10; ++j)
        {
            GRect brick = newGRect((j * 40) + 2, (i * 16) + 50, BRICK_WIDTH, BRICK_HEIGHT);
            setFilled(brick, true);
            
            switch(i)
            {
                case 0: setColor(brick, "BLUE");
                        setFillColor(brick, "BLUE");
                        break;
                case 1: setColor(brick, "CYAN");
                        setFillColor(brick, "CYAN");
                        break;
                case 2: setColor(brick, "GREEN");
                        setFillColor(brick, "GREEN");
                        break;
                case 3: setColor(brick, "YELLOW");
                        setFillColor(brick, "YELLOW");
                        break;
                case 4: setColor(brick, "ORANGE");
                        setFillColor(brick, "ORANGE");
                        break;
            }
            
            add(window, brick);
        }
    }
}

/**
 * Instantiates ball in center of window.  Returns ball.
 */
GOval initBall(GWindow window)
{
    // TODO
    GOval ball = newGOval(190, 322, RADIUS * 2, RADIUS * 2);
    
    if (ball != NULL)
    {
        setFilled(ball, true);
        setColor(ball, "BLACK");
        setFillColor(ball, "BLACK");
        add(window, ball);
        return ball;
    }
    
    return NULL;
}

/**
 * Instantiates paddle in bottom-middle of window.
 */
GRect initPaddle(GWindow window)
{
    // TODO
    GRect paddle = newGRect(167, 538, PADDLE_WIDTH, PADDLE_HEIGHT);
    
    if (paddle != NULL)
    {
        setFilled(paddle, true);
        setColor(paddle, "BLACK");
        setFillColor(paddle, "BLACK");
        add(window, paddle);
        return paddle;
    }
    
    return NULL;
}

/**
 * Instantiates, configures, and returns label for scoreboard.
 */
GLabel initScoreboard(GWindow window)
{
    // TODO
    GLabel score_board = newGLabel("0");
    
    if (score_board != NULL)
    {
        setFont(score_board, "SansSerif-48");       
        double x = (getWidth(window) - getWidth(score_board)) / 2;
        double y = (getHeight(window) - getHeight(score_board)) / 2;
        setLocation(score_board, x, y);
        setColor(score_board, "LIGHT_GRAY");
        add(window, score_board);
        return score_board;
    }
    
    return NULL;
}

/**
 * Updates scoreboard's label, keeping it centered in window.
 */
void updateScoreboard(GWindow window, GLabel label, int points)
{
    // update label
    char s[12];
    sprintf(s, "%i", points);
    setLabel(label, s);

    // center label in window
    double label_x = (getWidth(window) - getWidth(label)) / 2;
    double label_y = (getHeight(window) - getHeight(label)) / 2;
    setLocation(label, label_x, label_y);
}

/**
 * Detects whether ball has collided with some object in window
 * by checking the four corners of its bounding box (which are
 * outside the ball's GOval, and so the ball can't collide with
 * itself).  Returns object if so, else NULL.
 */
GObject detectCollision(GWindow window, GOval ball)
{
    // ball's location
    double x = getX(ball);
    double y = getY(ball);

    // for checking for collisions
    GObject object;

    // check for collision at ball's top-left corner
    object = getGObjectAt(window, x, y);
    if (object != NULL)
    {
        return object;
    }

    // check for collision at ball's top-right corner
    object = getGObjectAt(window, x + 2 * RADIUS, y);
    if (object != NULL)
    {
        return object;
    }

    // check for collision at ball's bottom-left corner
    object = getGObjectAt(window, x, y + 2 * RADIUS);
    if (object != NULL)
    {
        return object;
    }

    // check for collision at ball's bottom-right corner
    object = getGObjectAt(window, x + 2 * RADIUS, y + 2 * RADIUS);
    if (object != NULL)
    {
        return object;
    }

    // no collision
    return NULL;
}
