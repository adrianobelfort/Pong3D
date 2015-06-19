/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physics;

/**
 *
 * @author Adriano
 */
public class BallModel extends CollideableObject 
{
    final private float radius;
    private MovingModel movement;
    
    public BallModel(float xmin, float xmax, float zmin, float zmax, float radius)
    {
        super(xmin, xmax, zmin, zmax);
        this.radius = radius;
        movement = new MovingModel();
        CollisionAnalyzer.addObject(this);
    }
    
    public BallModel(float xmin, float xmax, float zmin, float zmax, float radius, float xspeed, float zspeed)
    {
        super(xmin, xmax, zmin, zmax);
        this.radius = radius;
        movement = new MovingModel(xspeed, zspeed);
        CollisionAnalyzer.addObject(this);
    }
    
    public float getRadius()
    {
        return radius;
    }
    
    public float rotationAngleAroundZ()
    {
        return (180.0f * xcenter)/(radius * 3.141592f);
    }
    
    public float rotationAngleAroundX()
    {
        return (180.0f * zcenter)/(radius * 3.141592f);
    }
    
    public void setSpeed(float xSpeed, float zSpeed)
    {
        movement.updateSpeed(xSpeed, zSpeed);
    }
    
    public float[] getSpeeds()
    {
        return movement.getSpeed();
    }
    
    @Override
    public boolean move(float xIncrement, float zIncrement)
    {
        if(!CollisionAnalyzer.analyzeCollision(this, xIncrement, zIncrement))
        {
            this.updatePosition(xIncrement, zIncrement);
            return true;
        }
        return false;
    }
    
    public boolean move(float timeStep)
    {
        float[] increments = movement.positionIncrement(timeStep); 
        
        return move(increments[0], increments[1]);
    }
}
