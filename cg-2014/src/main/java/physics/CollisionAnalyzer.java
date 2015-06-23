/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physics;

import java.util.ArrayList;

// TODO: ADD A STATIC VARIABLE TO KEEP TRACK OF WHICH POINT IS COLLIDING

/**
 * A class to analyze collisions. A collision will always be considered between a ball
 * and a parallelepiped. 
 * 
 * @author Adriano
 */
public class CollisionAnalyzer 
{
    private static ArrayList<ParallelepipedModel> worldObjects = new ArrayList<>();
    private static BallModel ball;
    private static ParallelepipedModel nearParallelepiped;
    private static ParallelepipedModel farParallelepiped;
    
    public static void addObject(BallModel object)
    {
        ball = object;
    }
    
    public static void addObject(ParallelepipedModel object)
    {
        worldObjects.add(object);
    }
    
    public static void registerNearFarParallelepipeds(ParallelepipedModel nPar, ParallelepipedModel fPar)
    {
        nearParallelepiped = nPar;
        farParallelepiped = fPar;
    }
    
    public static void printBoundaries(float[] objectBoundaries, float[] collidingObjectBoundaries)
    {
        System.out.println("Xmin: " + objectBoundaries[0] + ", " + collidingObjectBoundaries[0]);
        System.out.println("Xmax: " + objectBoundaries[1] + ", " + collidingObjectBoundaries[1]);
        System.out.println("Zmin: " + objectBoundaries[2] + ", " + collidingObjectBoundaries[2]);
        System.out.println("Zmax: " + objectBoundaries[3] + ", " + collidingObjectBoundaries[3]);
        System.out.println("");
    }
    
    
    public static boolean analyzeCollision(CollideableObject object)
    {
        return analyzeCollision(object, 0.0f, 0.0f);
    }
    
    public static boolean analyzeCollision(BallModel object)
    {
        return analyzeCollision(object, 0.0f, 0.0f);
    }
    
    public static boolean analyzeCollision(ParallelepipedModel object)
    {
        return analyzeCollision((CollideableObject) object);
    }
    
    
    // colisao entre 2 quadrados. Parametro coord indica qual a coordenada que se deseja realizar a analise da colisao
    public static boolean analyzeCollision(CollideableObject obj1, CollideableObject obj2, float xIn, float zIn)
    {
        float bound1[] = obj1.getBoundaries();
        float bound2[] = obj2.getBoundaries();
        
        if( (bound1[0]+xIn < bound2[1]) && (bound1[1]+xIn > bound2[0]) && (bound1[2]+zIn < bound2[3]) && (bound1[3]+zIn > bound2[2]))
        {
             return true;
        }
        else return false;
        
    }
    
    // 1 -> izquierda
    // 2 -> derecha
    // 0 -> cima
    // 0 -> baixo
    public static int analyzeCollisionSide(BallModel b, ParallelepipedModel p, boolean near, float xInc, float zInc)
    {
        float[] bCenter  = {b.getX(), b.getZ()};
        float[] pBounds = p.getBoundaries();
        
        boolean[] bMap = {(bCenter[1] > pBounds[3]), (bCenter[1] > pBounds[2]), (bCenter[0] > pBounds[1]), (bCenter[0] > pBounds[0])};
        
        // normal approaches
        if ((bMap[0] == false) && (bMap[1] == false) && (bMap[2] == false) && (bMap[3] == true))
            return 0;
        if ((bMap[0] == true) && (bMap[1] == true) && (bMap[2] == false) && (bMap[3] == true))    
            return 0;
        
        // left side approach
        if ((bMap[0] == false) && (bMap[1] == true) && (bMap[2] == false) && (bMap[3] == false))
            return 1;
        
        // right side approach
        if ((bMap[0] == false) && (bMap[1] == true) && (bMap[2] == true) && (bMap[3] == true))
            return 2;
        
        // senao, precisa interpolar =(
        // t = (pos - bc) / (bf - bc)
        // t[0,1,2,3] -> [xmin, xmax, zmin, zmax]
        float[] t = {
                (pBounds[0] - bCenter[0]) / xInc,
                (pBounds[1] - bCenter[0]) / xInc,
                (pBounds[2] - bCenter[1]) / zInc,
                (pBounds[3] - bCenter[1]) / zInc
            };
        
        // se for o plano near
        if (near == true)
        {
            // aprox frontal
            if((t[0] < t[2]) || (t[1] < t[2])) return 0;
            
            // aprox pela izquierda
            else if((t[2] < t[0]) && 
                    (bMap[0] == false) && (bMap[1] == false) && (bMap[2] == false) && (bMap[3] == false))
                return 1;
            // aprox pela derecha
            else if((t[2] < t[1]) &&
                    (bMap[0] == false) && (bMap[1] == false) && (bMap[2] == true) && (bMap[3] == true))
                return 2;
        }
        // se for o plano far
        else
        {
            // aprox traseira (ui)
            if((t[0] < t[3]) || (t[1] < t[3])) return 0;
            // aprox pela izquierda
            else if((t[3] < t[0]) &&
                    (bMap[0] == true) && (bMap[1] == true) && (bMap[2] == false) && (bMap[3] == false))
                return 1;
            // aprox pela derecha
            else if((t[3] < t[1]) &&
                    (bMap[0] == true) && (bMap[1] == true) && (bMap[2] == true) && (bMap[3] == true))
                return 2;
        }
        return 0;
    }
    
    public static boolean analyzeCollision(CollideableObject object, float xIncrement, float zIncrement)
    {
        
        for (ParallelepipedModel worldObject : worldObjects) 
            if (object != worldObject)
                if (analyzeCollision((CollideableObject) object, (CollideableObject) worldObject, xIncrement, zIncrement)) return true;
            
        
        /* create a way to determine if a ball is really colliding with another element
        tip: use the least-distance to a point to evaluate if a new calculation is needed
        */
        
        return false;
    }
    
    // analisa colisao da bola com qualquer outra coisa
    public static ParallelepipedModel analyzeCollisionFromBallWithAnything(BallModel object, float xIncrement, float zIncrement)
    {   
        for (ParallelepipedModel worldObject : worldObjects)
        {
            if (analyzeCollision((CollideableObject) object, (CollideableObject) worldObject, xIncrement, zIncrement))
            {
                return worldObject;
            }
        }
        return null;
    }
    
    // verifica colisao do bloco controlado pelo jogador e os blocos laterais
    public static ParallelepipedModel analyzeCollision(ParallelepipedModel object, float xIncrement, float zIncrement)
    {
        float[] objectBoundaries = object.getBoundaries();
        
        objectBoundaries[0] += xIncrement;
        objectBoundaries[1] += xIncrement;
        objectBoundaries[2] += zIncrement;
        objectBoundaries[3] += zIncrement;
        
        for (ParallelepipedModel worldObject : worldObjects) 
        {
            if (worldObject != object && worldObject != nearParallelepiped && worldObject != farParallelepiped)
            {
                float[] parallelepipedBoundaries = worldObject.getBoundaries();
                //printBoundaries(objectBoundaries, parallelepipedBoundaries);
                
                if (objectBoundaries[0] < parallelepipedBoundaries[1] && objectBoundaries[1] > parallelepipedBoundaries[0] &&
                        objectBoundaries[2] < parallelepipedBoundaries[3] && objectBoundaries[3] > parallelepipedBoundaries[2])
                {
                    //System.out.println("Collision would happen at " + (object.getX() + xIncrement) + ", " + (object.getZ() + zIncrement));
                    return worldObject;
                }
            }
        }
        
        return null;
    }
    
    public static ParallelepipedModel analyzeCollisionFromBallWithAnythingExceptObj1(BallModel object, float xIncrement, float zIncrement, ParallelepipedModel Obj1)
    {
        for (ParallelepipedModel worldObject : worldObjects) 
        {
            if (worldObject != Obj1)
                if (analyzeCollision((CollideableObject) object, (CollideableObject) worldObject, xIncrement, zIncrement))
                {
                    return worldObject;
                }
        }
        return null;
    }
    
    public static boolean analyzeCollisionWithBall(ParallelepipedModel object, float xIncrement, float zIncrement)
    {
        boolean collisionHappened = false, subcollisionHappened = true;
        
        float[] parallelepipedBoundaries = object.getBoundaries();
        float[] ballBoundaries = ball.getBoundaries();
        
        float computedDistanceX, computedDistanceZ, ballRadius;
        
        parallelepipedBoundaries[0] += xIncrement;
        parallelepipedBoundaries[1] += xIncrement;
        parallelepipedBoundaries[2] += zIncrement;
        parallelepipedBoundaries[3] += zIncrement;
        
        if (ballBoundaries[0] < parallelepipedBoundaries[1] && ballBoundaries[1] > parallelepipedBoundaries[0] &&
            ballBoundaries[2] < parallelepipedBoundaries[3] && ballBoundaries[3] > parallelepipedBoundaries[2])
        {
            //System.out.println("Collision with ball would happen at " + (object.getX() + xIncrement) + ", " + (object.getZ() + zIncrement));
            collisionHappened = true;
        }
                
        /*computedDistanceX = parallelepipedBoundaries[0] - ball.getX();
        computedDistanceZ = parallelepipedBoundaries[2] - ball.getZ();
        ballRadius = ball.getRadius();
        
        if (computedDistanceX * computedDistanceX + computedDistanceZ * computedDistanceZ < ballRadius)
        {
            return true;
        }
        
        computedDistanceX = parallelepipedBoundaries[1] - ball.getX();
        computedDistanceZ = parallelepipedBoundaries[2] - ball.getZ();
        
        if (computedDistanceX * computedDistanceX + computedDistanceZ * computedDistanceZ < ballRadius)
        {
            return true;
        }
        
        computedDistanceX = parallelepipedBoundaries[0] - ball.getX();
        computedDistanceZ = parallelepipedBoundaries[3] - ball.getZ();
        
        if (computedDistanceX * computedDistanceX + computedDistanceZ * computedDistanceZ < ballRadius)
        {
            return true;
        }
        
        computedDistanceX = parallelepipedBoundaries[1] - ball.getX();
        computedDistanceZ = parallelepipedBoundaries[3] - ball.getZ();
        
        if (computedDistanceX * computedDistanceX + computedDistanceZ * computedDistanceZ < ballRadius)
        {
            return true;
        }*/
        
        return collisionHappened;
    }
}
