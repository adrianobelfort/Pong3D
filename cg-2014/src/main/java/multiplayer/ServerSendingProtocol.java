package multiplayer;


import java.io.IOException;
import java.net.SocketException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Protocol for the player part of the communication with the server
 * @author Adriano
 */
public interface ServerSendingProtocol 
{
    public void connectToServer(String serverIP, int serverPort) throws IOException;
    public void disconnectFromServer() throws IOException;
    public void requestAvailabilityChange(boolean availability) throws IOException;
    public void requestToRegisterPlayer(String newNickname, int invitationPort) throws IOException;
    public void requestToUnregisterPlayer() throws IOException;
    public void requestListOfPlayers() throws IOException;
    public void sendDisconnectToServer() throws IOException;
    public void sendServerShutdown() throws IOException;
}
