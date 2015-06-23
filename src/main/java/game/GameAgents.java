/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import physics.BallModel;
import physics.CollisionAnalyzer;
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
    
    private float distanceFromCenter;
    private float parallelepipedLengthScale;
    private float zDistance;
    
    public CollisionAnalyzer analyzer;
    
    public GameAgents()
    {
        distanceFromCenter = 10.0f;
        parallelepipedLengthScale = 11.0f;
        zDistance = 16.0f;
        
        // should be here!
        //analyzer = new CollisionAnalyzer();
        
        ball = new BallModel(-0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.39f, 0.65f);
        leftWall = new ParallelepipedModel(-0.5f - distanceFromCenter, 0.5f - distanceFromCenter, -1.5f * parallelepipedLengthScale, 1.5f * parallelepipedLengthScale, new float[]{1.0f, 0.0f}, (0.5f - distanceFromCenter));
        rightWall = new ParallelepipedModel(-0.5f + distanceFromCenter, 0.5f + distanceFromCenter, -1.5f * parallelepipedLengthScale, 1.5f * parallelepipedLengthScale,  new float[]{-1.0f, 0.0f}, (-0.5f + distanceFromCenter));
        playerBlock = new ParallelepipedModel(-1.5f, 1.5f, zDistance - 0.5f, zDistance + 0.5f, new float[]{0.0f, -1.0f}, (zDistance - 0.5f));
        rivalBlock = new ParallelepipedModel(-1.5f, 1.5f, -(zDistance + 0.5f), -(zDistance - 0.5f), new float[]{0.0f, 1.0f}, (zDistance - 0.5f));
        playerBlock.bindBall(ball);
        rivalBlock.bindBall(ball);
    }
    
    public float getXDistance()
    {
        return distanceFromCenter;
    }
    
    public float getZDistance()
    {
        return zDistance;
    }
    
    public float getParallelepipedLengthScale()
    {
        return parallelepipedLengthScale;
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