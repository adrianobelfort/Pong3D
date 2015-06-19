package multiplayer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Adriano
 */
public class PlayerInfo 
{
    public String nickname;
    public String IP;
    public int listeningPort;
    boolean availability;
    
    PlayerInfo(String nickname, String IP, int listeningPort, boolean availability)
    {
        this.nickname = nickname;
        this.IP = IP;
        this.listeningPort = listeningPort;
        this.availability = true;
    }
    
    public void changeAvailability(boolean available)
    {
        availability = available;
    }
    
    public boolean isAvailable()
    {
        return availability;
    }
}
