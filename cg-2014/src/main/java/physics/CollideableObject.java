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
public abstract class CollideableObject 
{
    protected float xmin;
    protected float xmax;
    protected float zmin;
    protected float zmax;
    protected float xcenter;
    protected float zcenter;
    
    public CollideableObject(float xmin, float xmax, float zmin, float zmax)
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.zmin = zmin;
        this.zmax = zmax;
        this.xcenter = (xmin + xmax)/2.0f;
        this.zcenter = (zmin + zmax)/2.0f;
    }
    
    public float getX()
    {
        return xcenter;
    }
    
    public float getZ()
    {
        return zcenter;
    }
    
    public float[] getBoundaries()
    {
        return new float[]{xmin, xmax, zmin, zmax};
    }
    
    public void updatePosition(float xIncrement, float zIncrement)
    {
        xcenter += xIncrement;
        zcenter += zIncrement;
        xmin += xIncrement;
        xmax += xIncrement;
        zmin += zIncrement;
        zmax += zIncrement;
    }
    
    public void updateAbsolutePosition(float x, float z)
    {
        float pxmax = xmax, pxmin = xmin, pzmax = zmax, pzmin = zmin;
        
        xcenter = x;
        zcenter = z;
        xmax = xcenter + (pxmax - pxmin)/2.0f;
        xmin = xcenter - (pxmax - pxmin)/2.0f;
        zmax = zcenter + (pzmax - pzmin)/2.0f;
        zmin = zcenter - (pzmax - pzmin)/2.0f;
    }
    
    public abstract boolean move(float xIncrement, float zIncrement);
}
