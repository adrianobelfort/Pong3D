/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayer;

/**
 *
 * @author Adriano
 */
public abstract class MultiplayerAgent implements NetworkingConstants
{   
    public class NotConnectedException extends Exception
    {
        public NotConnectedException(String message)
        {
            super(message);
        }
        
        public NotConnectedException()
        {
            super();
        }
    }
}
