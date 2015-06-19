/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import com.jogamp.opengl.util.AnimatorBase;
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
    
    public GameState(AnimatorBase animator, Timer timer)
    {
        this.animator = animator;
        this.timer = timer;
        started = false;
        playing = false;
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
            animator.start();
            timer.start();
            return true;
        }
    }
    
    public void pauseGame(boolean requester)
    {
        playing = false;
        pauseRequester = requester;
        
        animator.pause();
        timer.stop();
    }
    
    public boolean resumeGame(boolean requester)
    {
        if (requester == pauseRequester)
        {
            playing = true;
            
            animator.resume();
            timer.start();
            return true;
        }
        else
        {
            return false;
        }
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
}
