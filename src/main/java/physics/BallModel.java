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
    
    public MovingModel getMovement() { return movement; }
    
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
    
    public boolean move(float timeStep)
    {
        float[] increment = movement.positionIncrement(timeStep);
        boolean playerControledBlocksCollision = false;
        boolean near = false;
        
        ParallelepipedModel colidedObj = CollisionAnalyzer.analyzeCollisionFromBallWithAnything(this, increment[0], increment[1]);
        if(colidedObj == null)
        {
            this.updatePosition(increment[0], increment[1]);
            return false;
        }
        
        // houve colisao com o objeto apontado por colidedObj
        else
        {
            float[] normal = colidedObj.getNormal().clone();
            float[] speed = this.movement.getSpeed();
            
            // correcao da posicao de colisao
            // primeiro, incrementa a posicao ate encostar na parede
            float[] newIncrement;
            float[] borders = colidedObj.getBoundaries();
            float[] ballBorders = this.getBoundaries();
            float step1;
            int side = 4;
            step1 = 0.5f * timeStep;
            
            
            //System.out.println("Normal antes das verifics: " + normal[0] + " " + normal[1]);
            
            // se for o plano esquerdo
            if (normal[0] == 1.0f)
            {
                step1 = (ballBorders[0] - borders[1]) / Math.abs(speed[0]);
            }
            // se for o plano direito
            else if(normal[0] == -1.0f)
            {
                step1 = (ballBorders[1] - borders[0]) / Math.abs(speed[0]);
            }
            //se for o plano far
            else if (normal[1] == 1.0f)
            {
                near = false;
                // descobrir qual o lado que houve a colisao
                side = CollisionAnalyzer.analyzeCollisionSide(this, colidedObj, near, increment[0], increment[1]);
                // se for pela esquerda, atualiza o vetor normal
                if (side == 1)     
                {
                    normal[0] =  1.0f;
                    normal[1] = 0.0f;
                    
                    step1 = (borders[0] - ballBorders[1]) / Math.abs(speed[0]);
                }
                //se for pela direita
                else if(side == 2)
                {
                    normal[0] = -1.0f;
                    normal[1] = 0.0f;
                    
                    step1 = (ballBorders[0] - borders[1]) / Math.abs(speed[0]);
                }
                else
                {
                    step1 = (ballBorders[2] - borders[3]) / Math.abs(speed[1]);
                }
                
                playerControledBlocksCollision = true;
                //System.out.println("Aproximacao frontal la atras");
            }
            // se for o plano near
            else if(normal[1] == -1.0f)
            {
                near = true;
                System.out.println("Achou o plano near!");
                // descobrir o lado q houve a colisao
                side = CollisionAnalyzer.analyzeCollisionSide(this, colidedObj, near, increment[0], increment[1]);
                
                // se for pela esquerda, atualiza o vet normal
                if (side == 1)
                { 
                    normal[0] = -1.0f;
                    normal[1] = 0.0f;
                    System.out.println("    Esquerda");
                    step1 = (borders[0] - ballBorders[1]) / Math.abs(speed[0]);
                }
                // pela direita
                else if(side == 2)
                { 
                    System.out.println("    Direita");
                    normal[0] =  1.0f;
                    normal[1] = 0.0f;
                    
                    step1 = (ballBorders[0] - borders[1]) / Math.abs(speed[0]);
                }
                else
                {
                    step1 = (ballBorders[3] - borders[2]) / Math.abs(speed[1]); 
                }
                playerControledBlocksCollision = true;
                //System.out.println("Aproximacao traseira aqui na frente");
            }
            
            if (side == 0) System.out.println("Aproximacao normal (fronteira ou traseira[ui])");
            if (side == 1) System.out.println("Aproximacao pela izquierda");
            if (side == 2) System.out.println("Aproximacao pela derecha");
            // atualiza a velocidade
            // Vout = N . (2N . Vin) - L
            float[] newspeed = {-((normal[0] * (2*normal[0] * speed[0])) - speed[0]), -((normal[1] * (2*normal[1] * speed[1])) - speed[1])};
       
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
            //>>>>>>>>>>>>>>>>>>>>>> INCREMENTO DA VELOCIDADE
            //>>>>>>>>>>>>>>>>>>>>>> DESCOMENTAR DEPOIS DA ETAPA DE TESTES
            
            if (playerControledBlocksCollision)
                   this.movement.incrementAbsSpeed(0.2f);
                    
            //this.movement.positionIncrement(step2);
            //return true;
            return this.move(0.2f * timeStep, colidedObj) || (playerControledBlocksCollision && near);
        }   
    }

    public boolean move(float step2, ParallelepipedModel colidedObj1) {
        float[] Increment = this.movement.positionIncrement(step2);
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
        ParallelepipedModel colidedObj = CollisionAnalyzer.analyzeCollisionFromBallWithAnything(this, xIncrement, zIncrement);
        if(colidedObj == null)
        {
            this.updatePosition(xIncrement, zIncrement);
            return false;
        }
        
        else
        {
            float[] speed = this.getSpeeds();
            // se estiver saindo pelo plano near, cospe a bola pra fora
            if (speed[1] >= 0) this.updatePosition(0.0f, Math.abs(xIncrement));
            
            // se estiver saindo pelo plano far, tbm cospe a bola pra fora
            else if (speed[1] < 0) this.updatePosition(0.0f, -(Math.abs(xIncrement)));
            return true;
        }
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
}
