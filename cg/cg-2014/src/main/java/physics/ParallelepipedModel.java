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
    public ParallelepipedModel(float xmin, float xmax, float zmin, float zmax)
    {
        super(xmin, xmax, zmin, zmax);
        CollisionAnalyzer.addObject(this);
    }
}
