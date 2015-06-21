/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import com.jogamp.opengl.util.AnimatorBase;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author Adriano
 */
public class GameState 
{
    public Timer timer;
    public AnimatorBase animator;
    public boolean started;
    public boolean playing;
    public boolean pauseRequester;  // true: player; false: network
    public int playerScore;
    public int rivalScore;
    public boolean winner;
    public boolean bound;
    public Thread animatorThread;
    
    public GameState(final AnimatorBase animator, Timer timer)
    {
        this.animator = animator;
        this.timer = timer;
        started = false;
        playing = false;
        bound = false;
    }
    
    public void beginAnimation()
    {
        animatorThread = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                //timer.start();
                animator.start();
            }
        });
        animatorThread.start();

        /*try {
            this.wait(0, 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                animator.pause();
            }
        }).start();
    }
    
    public void bind()
    {
        bound = true;
    }
    
    public void unbind()
    {
        bound = false;
    }
    
    public boolean isBound()
    {
        return bound;
    }
    
    public void pauseTimer()
    {
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                timer.stop();
            }
        }).start();
    }
    
    public void resumeTimer()
    {
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                timer.start();
            }
        }).start();
    }
    
    public boolean startGame()
    {
        if (started)
        {
            return false;
        }
        else
        {
            started = true;
            
            new Thread(new Runnable() 
            {
                @Override
                public void run() 
                {
                    if (!timer.isRunning()) timer.start();
                    if (!animator.isAnimating()) animator.resume();
                }
            }).start();
            return true;
        }
    }
    
    public boolean pauseGame(boolean requester)
    {
        if (playing)
        {
            new Thread(new Runnable() 
            {
                @Override
                public void run() 
                {
                    timer.stop();
                    animator.pause();
                }
            }).start();
        
            playing = false;
            pauseRequester = requester;
            
            return true;
        }
        
        return false;
    }
    
    public boolean resumeGame(boolean requester)
    {
        if (requester == pauseRequester && !playing)
        {
            playing = true;
            
            new Thread(new Runnable() 
            {
                @Override
                public void run() 
                {
                    timer.start();
                    animator.resume();
                }
            }).start();
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void stopGame()
    {
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                timer.stop();
                animator.stop();
            }
        }).start();
    }
    
    public int getPlayerScore()
    {
        return playerScore;
    }
    
    public int getRivalScore()
    {
        return rivalScore;
    }
    
    public int checkForWinners()
    {
        if (playerScore >= 7)
        {
            winner = true;
            return 1;       // This player wins
        }
        else if (rivalScore >= 7)
        {
            winner = false;
            return -1;      // The rival wins
        }
        else
        {
            return 0;       // No one has won yet
        }
    }
    
    public boolean whoWon()
    {
        return winner;
    }
    
    public boolean isPaused()
    {
        return playing == false;
    }
}
