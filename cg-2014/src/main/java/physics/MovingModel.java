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
public class MovingModel 
{
    private final float speed[];
    
    MovingModel()
    {
        speed = new float[2];
    }
    
    MovingModel(float initialSpeeds[])
    {
        speed = (float[]) initialSpeeds.clone();
    }
    
    MovingModel (float xSpeed, float zSpeed)
    {
        speed = new float[]{xSpeed, zSpeed};
    }
    
    public float[] positionIncrement(float timeStep)
    {
        // Simple ds = v * dt
        return new float[]{speed[0] * timeStep, speed[1] * timeStep};
    }
    
    public void incrementSpeed(float xIncrement, float zIncrement)
    {
        speed[0] += xIncrement;
        speed[1] += zIncrement;
    }
    
    public void incrementAbsSpeed(float Inc)
    {
        if(speed[0] > 0) speed[0] += Inc;
        else speed[0] -= Inc;
        if(speed[1] > 0) speed[1] += Inc;
        else speed[1] -= Inc;
    }
    
    public void incrementSpeedPercent(float perc)
    {
        speed[0] *= (perc+1.0f);
        speed[1] *= (perc+1.0f);
    }
    
    public void updateSpeed(float xSpeed, float zSpeed)
    {
        speed[0] = xSpeed;
        speed[1] = zSpeed;
    }
    
    public float[] getSpeed()
    {
        return (float[]) speed.clone();
    }
}
