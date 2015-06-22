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
    public ParallelepipedModel(float xmin, float xmax, float zmin, float zmax, float[] normal)
    {
        super(xmin, xmax, zmin, zmax, normal);
        CollisionAnalyzer.addObject(this);
    }
    
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
