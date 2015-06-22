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
    private final float timeStep;
    
    public BallModel(float xmin, float xmax, float zmin, float zmax, float radius, float timeStep)
    {
        super(xmin, xmax, zmin, zmax, new float[2]);
        this.radius = radius;
        this.timeStep = timeStep;
        movement = new MovingModel();
        CollisionAnalyzer.addObject(this);
    }
    
    public BallModel(float xmin, float xmax, float zmin, float zmax, float radius, float xspeed, float zspeed, float timeStep)
    {
        super(xmin, xmax, zmin, zmax, new float[2]);
        this.radius = radius;
        this.timeStep = timeStep;
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
        ParallelepipedModel colidedObj = CollisionAnalyzer.analyzeCollisionFromBallWithAnything(this, xIncrement, zIncrement);
        if(colidedObj == null)
        {
            this.updatePosition(xIncrement, zIncrement);
            return true;
        }
        
        // houve colisao com o objeto apontado por colidedObj
        else
        {
            float[] normal = colidedObj.getNormal();
            float[] speed = this.movement.getSpeed();
            // atualiza a velocidade
            // Vout = N . (2N . Vin) - L
            this.movement.updateSpeed((normal[0] * (2*normal[0] * speed[0])) - speed[0], (normal[1] * (2*normal[1] * speed[1])) - speed[1]);
            
            // correcao da posicao de colisao
            // se for um dos planos near/far
            if (normal[0] != 0.0)
            {
                this.updateAbsolutePosition(this.getX() + xIncrement - speed[0]*timeStep + (colidedObj.getZ() + normal[1] * colidedObj.getZdist())/speed[1] , colidedObj.getZ() + normal[1] * (colidedObj.getZdist() + radius));
            }
            else
            {
                this.updateAbsolutePosition(colidedObj.getZ() + normal[1] * (colidedObj.getXdist() + radius), this.getZ() + zIncrement - speed[0]*timeStep + (colidedObj.getX() + normal[1] * colidedObj.getXdist())/speed[1] );
            }
            return false;
        }
        
    }
    
    public boolean move(float timeStep)
    {
        float[] increments = movement.positionIncrement(timeStep); 
        
        return move(increments[0], increments[1]);
    }
}
