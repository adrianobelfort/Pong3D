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
    private float innerBound;
    private BallModel ball;
   
    public ParallelepipedModel(float xmin, float xmax, float zmin, float zmax, float[] normal, float in_bound)
    {
        super(xmin, xmax, zmin, zmax, normal);
        innerBound = in_bound;
        CollisionAnalyzer.addObject(this);
    }
    public void bindBall(BallModel b) { ball = b; }
    
    public float getInnerBound() { return innerBound; }
    
    @Override
    public boolean move(float xIncrement, float zIncrement)
    {
        float[] bounds = this.getBoundaries();
        // se houve colisao com uma parede
        ParallelepipedModel p = CollisionAnalyzer.analyzeCollision(this, xIncrement, zIncrement);
        if (p != null)
        {
            // se esta indo na direcao positiva
            if (xIncrement > 0) this.updatePosition(p.getInnerBound() - bounds[1], zIncrement);
            else if(xIncrement <= 0) this.updatePosition(p.getInnerBound() - bounds[0], zIncrement);
            return false;
        }
        
        // se houve colisao com a bola
        
        if (CollisionAnalyzer.analyzeCollisionWithBall(this, xIncrement, zIncrement))
        {
            float[] ballSpeed = ball.getSpeeds();
            float[] ballBounds = ball.getBoundaries();
            
            // verifica se a bola esta indo na mesma direcao do deslocamento (em x, positivo)
            if ((ballSpeed[0] >= 0.0f) && (xIncrement > 0.0f))
            {
                // aumenta a velocidade da bola
                ball.getMovement().incrementSpeed(xIncrement, 0.0f);
                // tira a bola do caminho do bloco
                ball.move(ballBounds[0] - bounds[1], 0.0f);
                // coloca o bloco onde ele queria ir
                this.updatePosition(xIncrement, zIncrement);
            }
            // bola e bloco indo na direcao negativa
            else if((ballSpeed[0] <= 0.0f) && (xIncrement < 0.0f))
            {
                ball.getMovement().incrementSpeed(xIncrement, 0.0f);
                //System.out.println("step = " + (bounds[0] - ballBounds[1]));
                ball.move(ballBounds[1] - bounds[0], 0.0f);
                this.updatePosition(xIncrement, zIncrement);
            }
            
            // bola indo na direcao negativa e bloco na direcao positiva
            else if((ballSpeed[0] <= 0.0f) && (xIncrement >= 0.0f))
            {
                this.updatePosition(ballBounds[0] - bounds[1], zIncrement);
                ball.move(xIncrement);
                ball.getMovement().incrementSpeed(xIncrement, 0.0f);
                ball.move(xIncrement, 0.0f);
                this.updatePosition((ballBounds[0] - bounds[1]), 0.0f);
            }
            
            // bola indo na direcao positiva e bloco na direcao negativa
            else if((ballSpeed[0] >= 0.0f) && (xIncrement <= 0.0f))
            {
                this.updatePosition(ballBounds[1] - bounds[0], zIncrement);
                ball.move(-xIncrement);
                ball.getMovement().incrementSpeed(xIncrement, 0.0f);
                ball.move(xIncrement, 0.0f);
                this.updatePosition((ballBounds[1] - bounds[0]), 0.0f);
            }
            return true;
        }
        else
        {
            this.updatePosition(xIncrement, zIncrement);
            return false;
        }
        //return false;
    }

    float getZdist() {
        return zdist;
    }

    float getXdist() {
        return xdist;
    }
}
