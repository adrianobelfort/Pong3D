/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physics;

import app.Pong;

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
    
    public void setRandomSpeed()
    {
        float xspeed, zspeed, choice;
        
        choice = (float) Math.random();
        xspeed = (float) Math.random();
        zspeed = (float) Math.random();
        
        if (choice < 0.25f)
        {
            xspeed = -xspeed;
            zspeed = -zspeed;
        }
        else if (choice >= 0.25f && choice < 0.5f)
        {
            // x is the same
            zspeed = -zspeed;
        }
        else if (choice >= 0.5f && choice < 0.75f)
        {
            xspeed = -xspeed;
            // z is the same
        }
        else if (choice >= 0.75f)
        {
            // both x and z are the same
        }
        
        setSpeed(xspeed, zspeed);
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
        boolean collisionWithPlayerBlock = false;
        
        ParallelepipedModel colidedObj = CollisionAnalyzer.analyzeCollisionFromBallWithAnything(this, increment[0], increment[1]);
        if(colidedObj == null)
        {
            this.updatePosition(increment[0], increment[1]);
            return false;
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
            float[] borders = colidedObj.getBoundaries();
            float[] ballBorders = this.getBoundaries();
            float step1;
            step1 = 0.5f * timeStep;
            // se for o plano direito
            if (normal[0] == 1.0f)
            {
                step1 = (ballBorders[0] - borders[1]) / Math.abs(speed[0]); 
                Pong.collisionWithWall = true;
                System.out.println("Aproximacao lateral na direita");
            }
            // se for o plano esquerdo
            else if(normal[0] == -1.0f)
            {
                step1 = (ballBorders[1] - borders[0]) / Math.abs(speed[0]); 
                Pong.collisionWithWall = true;
                System.out.println("Aproximacao lateral na izquierda");
            }
            //se for o plano far
            else if (normal[1] == 1.0f)
            {
                step1 = (ballBorders[2] - borders[3]) / Math.abs(speed[1]);
                Pong.collisionWithWall = false;
                collisionWithPlayerBlock = true;
                System.out.println("Aproximacao frontal la atras");
            }
            // se for o plano near
            else if(normal[1] == -1.0f)
            {
                step1 = (ballBorders[3] - borders[2]) / Math.abs(speed[1]); 
                Pong.collisionWithWall = false;
                collisionWithPlayerBlock = true;
                System.out.println("Aproximacao traseira aqui na frente");
            }
/*
            if (normal[1] != 0.0f)
            {
                step1 = ((Math.abs(this.getZ() - colidedObj.getInnerBound()) - this.getRadius()) / Math.abs(speed[1])); 
                newIncrement = this.movement.positionIncrement(step1); 
                System.out.println("1ST COLISION: Step" + timeStep + "  Step1 " + step1);
            }
            else
            {
                step1 = ((Math.abs(this.getX() - colidedObj.getInnerBound()) - this.getRadius()) / Math.abs(speed[0]));
                newIncrement = this.movement.positionIncrement(step1); 
                System.out.println("2nd collision Step" + timeStep + "  Step1 " + step1);
            }*/
            
            newIncrement = this.movement.positionIncrement(step1); 
            float[] bounds = this.getBoundaries();
            float[] objBounds = colidedObj.getBoundaries();
            
            /*System.out.println("Xcenter = " + this.getX() + "xbounds: " + bounds[0] + ", " + bounds[1]);
            System.out.println("Xbord = " + colidedObj.getInnerBound() + "objxmin= " + objBounds[0] + "objxmax= " + objBounds[1]);
            System.out.println("xc - xbord = " + Math.abs(this.getX() - colidedObj.getInnerBound()));
            System.out.println("|xc - xbord| - rad = " + (Math.abs(this.getX() - colidedObj.getInnerBound()) - this.getRadius()));
            System.out.println("speeds: " + speed[0] + ", " + speed[1]);
            System.out.println("Xincrement = " + increment[0]);
            System.out.println("timeStep = " + timeStep);
            System.out.println("speed . timestep (x) = " + (speed[0]*timeStep));*/
            //System.out.println("Step" + timeStep + "  Step1 " + step1);
            this.updatePosition(newIncrement[0], newIncrement[1]);
            
            // depois, ira executar o proximo movimento em direcao a outra parede
            //step2 = timeStep - step1;
            this.movement.updateSpeed(newspeed[0], newspeed[1]);
            if (collisionWithPlayerBlock)
                   this.movement.incrementAbsSpeed(0.2f);
            //this.movement.positionIncrement(step2);
            //return true;
            return this.move(0.3f * timeStep, colidedObj) || collisionWithPlayerBlock;
        }   
    }

    public boolean move(float step2, ParallelepipedModel colidedObj1) {
        float[] Increment = this.movement.positionIncrement(step2);
        
        Pong.collisionWithWall = false;
        ParallelepipedModel colidedObj2 = CollisionAnalyzer.analyzeCollisionFromBallWithAnythingExceptObj1(this, Increment[0], Increment[1], colidedObj1);
        if (colidedObj2 == null)
        {
            this.movement.positionIncrement(step2);   
            return false;
        }
        else
        {
            float[] normal = colidedObj2.getNormal();
            float[] speed = this.movement.getSpeed();
            System.out.println("COLIDIU PELA 2A VEZ!!!");
            // atualiza a velocidade
            // Vout = N . (2N . Vin) - L
            this.movement.updateSpeed((normal[0] * (2*normal[0] * speed[0])) - speed[0], (normal[1] * (2*normal[1] * speed[1])) - speed[1]);
            this.movement.positionIncrement(step2);
            return true;
        }
    }

    @Override
    public boolean move(float xIncrement, float zIncrement) {
        return true;
    }
}
