package multiplayer;


import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Adriano
 */
public interface PlayerReceivingProtocol 
{
    public void receiveCollision() throws IOException;
    public void receiveRequestToPlay() throws IOException;
    public boolean receiveResponseToPlay() throws IOException;
    public void receiveGameStart() throws IOException;
    public void receiveBlockMove() throws IOException;
    public void receivePointsScored() throws IOException;
    public void receiveDisconnectFromPlayer() throws IOException;
    public void receiveGamePause() throws IOException;
    public void receiveGameResume() throws IOException;
}