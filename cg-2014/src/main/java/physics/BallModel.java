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

    public BallModel(float xmin, float xmax, float zmin, float zmax, float radius, float timeStep)
    {
        super(xmin, xmax, zmin, zmax, new float[2]);
        this.radius = radius;
        movement = new MovingModel();
        CollisionAnalyzer.addObject(this);
    }
    
    public BallModel(float xmin, float xmax, float zmin, float zmax, float radius, float xspeed, float zspeed)
    {
        super(xmin, xmax, zmin, zmax, new float[2]);
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
    
   /* @Override
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
            //System.out.println("Antes da colisao" + this.movement.getSpeed()[0] + " " + this.movement.getSpeed()[1]);
            this.movement.updateSpeed((normal[0] * (2*normal[0] * speed[0])) - speed[0], (normal[1] * (2*normal[1] * speed[1])) - speed[1]);
            //System.out.println("depois da colisao" + this.movement.getSpeed()[0] + " " + this.movement.getSpeed()[1]);
            
            //this.movement.positionIncrement(timeStep);
           

            // correcao da posicao de colisao
            float[] newIncrement1;
            float[] newIncrement2;
            float step1, step2;
            // se for um dos planos near/far
            if (normal[0] != 0.0)
            {
                step1 = Math.abs(this.getZ() - (colidedObj.getZ() + normal[1] * colidedObj.getZdist()));
                step2 = timeStep - step1;
                newIncrement1 = this.movement.positionIncrement(step1);
                
            }
            else
            {
                this.updateAbsolutePosition(colidedObj.getZ() + normal[1] * (colidedObj.getXdist() + radius), this.getZ() + zIncrement - speed[0]*timeStep + (colidedObj.getX() + normal[1] * colidedObj.getXdist())/speed[1] );
            }
            return false;
        }
    }*/
    
    public boolean move(float timeStep)
    {
        float[] increment = movement.positionIncrement(timeStep); 
        
        ParallelepipedModel colidedObj = CollisionAnalyzer.analyzeCollisionFromBallWithAnything(this, increment[0], increment[1]);
        if(colidedObj == null)
        {
            this.updatePosition(increment[0], increment[1]);
            return true;
        }
        
        // houve colisao com o objeto apontado por colidedObj
        else
        {
            float[] normal = colidedObj.getNormal();
            float[] speed = this.movement.getSpeed();
            
            // atualiza a velocidade
            // Vout = N . (2N . Vin) - L
            float[] newspeed = {-((normal[0] * (2*normal[0] * speed[0])) - speed[0]), -((normal[1] * (2*normal[1] * speed[1])) - speed[1])};
       
           

            // correcao da posicao de colisao
            // primeiro, incrementa a posicao ate encostar na parede
            float[] newIncrement;
            float step1, step2;
            // se for um dos planos near/far
            if (normal[0] != 0.0)
            {
                step1 = 1/(Math.abs((Math.abs(this.getZ() - colidedObj.getInnerBound()) - this.getRadius()) / speed[1])); 
                
                newIncrement = this.movement.positionIncrement(step1);       
            }
            else
            {
                step1 = 1/(Math.abs((Math.abs(this.getX() - colidedObj.getInnerBound()) - this.getRadius()) / speed[0]));
                newIncrement = this.movement.positionIncrement(step1); 
                
            }
            System.out.println("Step" + timeStep + "  Step1 " + step1);
            this.updatePosition(newIncrement[0], newIncrement[1]);
            
            // depois, ira executar o proximo movimento em direcao a outra parede
            step2 = timeStep - step1;
            newIncrement = this.movement.positionIncrement(step2);
            this.movement.updateSpeed(newspeed[0], newspeed[1]);
            //this.movement.positionIncrement(step2);
            //return true;
            return this.move(newIncrement[0], newIncrement[1], colidedObj);
        }   
    }

    public boolean move(float xIncrement, float zIncrement, ParallelepipedModel colidedObj1) {
        ParallelepipedModel colidedObj2 = CollisionAnalyzer.analyzeCollisionFromBallWithAnythingExceptObj1(this, xIncrement, zIncrement, colidedObj1);
        
        if (colidedObj2 == null)
        {
            this.updatePosition(xIncrement, zIncrement);
            return true;
        }
        else
        {
            float[] normal = colidedObj2.getNormal();
            float[] speed = this.movement.getSpeed();
            
            // atualiza a velocidade
            // Vout = N . (2N . Vin) - L
            this.movement.updateSpeed((normal[0] * (2*normal[0] * speed[0])) - speed[0], (normal[1] * (2*normal[1] * speed[1])) - speed[1]);
            this.updatePosition(xIncrement, zIncrement);
        }
        
        return true;
    }

    @Override
    public boolean move(float xIncrement, float zIncrement) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
