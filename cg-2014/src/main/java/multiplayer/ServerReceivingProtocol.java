package multiplayer;


import java.io.IOException;
import java.net.SocketException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Adriano
 */
public interface ServerReceivingProtocol 
{
    public void receiveAvailabilityChange() throws SocketException, IOException;
    public boolean registerNickname() throws SocketException, IOException;
    public void unregisterNickname();
    public void notifyInvalidNickname() throws IOException;
    public void notifyAcceptedNickname() throws IOException;
    public void sendPlayersList() throws IOException;
    public void receiveDisconnectFromPlayer() throws IOException;
    public void receiveServerShutdown() throws IOException;
}
