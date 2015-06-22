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
public class ParallelepipedModel extends CollideableObject 
{
    private static float innerBound;
   
    public ParallelepipedModel(float xmin, float xmax, float zmin, float zmax, float[] normal, float in_bound)
    {
        super(xmin, xmax, zmin, zmax, normal);
        innerBound = in_bound;
        CollisionAnalyzer.addObject(this);
    }
    
    public float getInnerBound() { return innerBound; }
    
    @Override
    public boolean move(float xIncrement, float zIncrement)
    {
        if (!CollisionAnalyzer.analyzeCollision(this, xIncrement, zIncrement) &&
            !CollisionAnalyzer.analyzeCollisionWithBall(this, xIncrement, zIncrement))
        {
            this.updatePosition(xIncrement, zIncrement);
            return true;
        }
        return false;
    }

    float getZdist() {
        return zdist;
    }

    float getXdist() {
        return xdist;
    }
}
