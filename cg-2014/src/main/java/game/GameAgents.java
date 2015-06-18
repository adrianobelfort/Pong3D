/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import physics.BallModel;
import physics.ParallelepipedModel;

/**
 *
 * @author Adriano
 */
public class GameAgents 
{
    private final BallModel ball;
    private final ParallelepipedModel playerBlock;
    private final ParallelepipedModel rivalBlock;
    private final ParallelepipedModel leftWall;
    private final ParallelepipedModel rightWall;
    
    public GameAgents(BallModel ball, ParallelepipedModel playerBlock, ParallelepipedModel rivalBlock, ParallelepipedModel leftWall, ParallelepipedModel rightWall)
    {
        this.ball = ball;
        this.playerBlock = playerBlock;
        this.rivalBlock = rivalBlock;
        this.leftWall = leftWall;
        this.rightWall = rightWall;
    }
    
    public BallModel getBall()
    {
        return ball;
    }
    
    public ParallelepipedModel getPlayerBlock()
    {
        return playerBlock;
    }
    
    public ParallelepipedModel getRivalBlock()
    {
        return rivalBlock;
    }
    
    public ParallelepipedModel getLeftWall()
    {
        return leftWall;
    }
    
    public ParallelepipedModel getRightWall()
    {
        return rightWall;
    }
}