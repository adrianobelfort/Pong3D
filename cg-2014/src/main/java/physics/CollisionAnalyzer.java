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
    private static ArrayList<ParallelepipedModel> worldObjects;
    private static BallModel ball;
    private static ParallelepipedModel nearParallelepiped;
    private static ParallelepipedModel farParallelepiped;
    
    public CollisionAnalyzer()
    {
        worldObjects = new ArrayList<>();
    }
    
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
        return (bound1[0]+xIn < bound2[1]) && (bound1[1]+xIn > bound2[0]) && (bound1[2]+zIn < bound2[3]) && (bound1[3]+zIn > bound2[2]);
    }
    
    public static boolean analyzeCollision(BallModel obj1, CollideableObject obj2)
    {
        
        return false;
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
    public static boolean analyzeCollision(ParallelepipedModel object, float xIncrement, float zIncrement)
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
                    return true;
                }
            }
        }
        
        return false;
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
