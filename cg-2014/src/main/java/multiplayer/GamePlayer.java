/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: FIND A WAY TO INTERRUPT THE ACCEPTING THREAD WHEN A CONNECTION TO A PLAYER IS ESTABLISHED

/**
 *
 * @author Adriano
 */
public class GamePlayer extends MultiplayerAgent 
{
    private Socket serverSocket;
    private Socket rivalSocket;
    private DataInputStream serverInputStream;
    private DataOutputStream serverOutputStream;
    private DataInputStream rivalInputStream;
    private DataOutputStream rivalOutputStream;
    private boolean playerAvailability;
    private Thread connectionWaiter;
    
    GamePlayer()
    {
        playerAvailability = true;
        serverSocket = null;
        rivalSocket = null;
    }
    
    public void guaranteePlayerConnection() throws NotConnectedException
    {
        if (rivalSocket == null)
        {
            throw new NotConnectedException("Not connected to a player");
        }
        else if (rivalSocket.isClosed())
        {
            throw new NotConnectedException("Not connected to a player");
        }
    }
    
    public void guaranteeServerConnection() throws NotConnectedException
    {
        if (serverSocket == null)
        {
            throw new NotConnectedException("Not connected to a server");
        }
        else if (serverSocket.isClosed())
        {
            throw new NotConnectedException("Not connected to a server");
        }
    }    
    
    public boolean connectToServer(String serverIP)
    {
        System.out.println("Connecting to server " + serverIP + " at port " + serverListeningPort);
        
        try 
        {
            serverSocket = new Socket(serverIP, serverListeningPort);
            
            serverInputStream = new DataInputStream(new BufferedInputStream(serverSocket.getInputStream()));
            serverOutputStream = new DataOutputStream(new BufferedOutputStream(serverSocket.getOutputStream()));
        } 
        catch (IOException ex) 
        {
            System.out.println("Could not establish connection with the server");
            return false;
        }
        
        return true;
    }
    
    public void disconnectFromServer() throws NotConnectedException, IOException
    {
        if (serverSocket.isClosed())
        {
            throw new NotConnectedException("Exception: tried to disconnect from a closed socket");
        }
        
        System.out.println("Closing connection with the server");
        serverInputStream.close();
        serverOutputStream.close();
        serverSocket.close();
    }
    
    public boolean connectToPlayer(String playerIP)
    {
        try 
        {
            rivalSocket = new Socket(playerIP, playerListeningPort);
            
            rivalInputStream = new DataInputStream(new BufferedInputStream(rivalSocket.getInputStream()));
            rivalOutputStream = new DataOutputStream(new BufferedOutputStream(rivalSocket.getOutputStream()));
        } 
        catch (IOException ex) 
        {
            System.out.println("Could not establish connection with the peer at " + playerIP + ", port " + playerListeningPort);
            return false;
        }
        
        return true;
    }
    
    public void disconnectFromPlayer() throws NotConnectedException, IOException
    {
        if (rivalSocket.isClosed())
        {
            throw new NotConnectedException("Exception: tried to disconnect from a closed socket");
        }
        
        System.out.println("Closing connection with the rival");
        rivalInputStream.close();
        rivalOutputStream.close();
        rivalSocket.close();
    }
    
    public void waitForConnections()
    {
        connectionWaiter = new Thread (new Runnable()
        {
            @Override
            public void run()
            {
                try 
                {
                    ServerSocket incomingConnection = new ServerSocket(playerListeningPort);
                    
                    Socket tentativeRivalSocket = incomingConnection.accept();
                    if (rivalSocket == null)
                    {
                        rivalSocket = tentativeRivalSocket;
                        
                        rivalInputStream = new DataInputStream(new BufferedInputStream(rivalSocket.getInputStream()));
                        rivalOutputStream = new DataOutputStream(new BufferedOutputStream(rivalSocket.getOutputStream()));
                    }
                    else if (rivalSocket.isClosed())
                    {
                        rivalSocket = tentativeRivalSocket;
                        
                        rivalInputStream = new DataInputStream(new BufferedInputStream(rivalSocket.getInputStream()));
                        rivalOutputStream = new DataOutputStream(new BufferedOutputStream(rivalSocket.getOutputStream()));
                    }
                    else
                    {
                        tentativeRivalSocket.close();
                    }
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(GamePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        connectionWaiter.start();
    }
    
    public void changeAvailability(boolean available) throws NotConnectedException
    {
        if (serverSocket == null)
        {
            throw new NotConnectedException("No socket was created before changing availability");
        }
        else if (!serverSocket.isClosed())
        {
            throw new NotConnectedException("The player is not connected to a server");
        }
        
        try 
        {
            serverOutputStream.writeShort(changingAvailability);
            serverOutputStream.writeBoolean(available);
            serverOutputStream.flush();
            
            playerAvailability = available;      
        } 
        catch (IOException ex) 
        {
            System.err.println("Error while changing availability");
        }
    }
    
    public void sendRequestToPlay() throws NotConnectedException, IOException
    {
        guaranteePlayerConnection();
        
        rivalOutputStream.writeShort(invitationToPlay);
        rivalOutputStream.flush();
    }
    
    public void sendResponseToPlay(boolean response) throws NotConnectedException, IOException
    {
        guaranteePlayerConnection();
        
        rivalOutputStream.writeShort(responseToInvitationToPlay);
        rivalOutputStream.writeBoolean(response);
        rivalOutputStream.flush();
        
        if (response == false)
        {
            disconnectFromPlayer();
        }
    }
    
    public void sendGameStart(float[] initialSpeeds) throws NotConnectedException, IOException
    {
        guaranteePlayerConnection();
        
        rivalOutputStream.writeShort(gameStarts);
        rivalOutputStream.writeFloat(initialSpeeds[0]);
        rivalOutputStream.writeFloat(initialSpeeds[1]);
        rivalOutputStream.flush();
    }
    
    public void sendBlockMove(float xIncrement, float zIncrement) throws NotConnectedException, IOException
    {
        guaranteePlayerConnection();
        
        rivalOutputStream.writeShort(movingBlock);
        rivalOutputStream.writeFloat(xIncrement);
        rivalOutputStream.writeFloat(zIncrement);
        rivalOutputStream.flush();
    }
    
    public void sendCollision(float[] newSpeeds) throws IOException, NotConnectedException
    {
        guaranteePlayerConnection();
        
        rivalOutputStream.writeShort(ballCollision);
        rivalOutputStream.writeFloat(newSpeeds[0]);
        rivalOutputStream.writeFloat(newSpeeds[1]);
        rivalOutputStream.flush();
    }
    
    public void sendPointsScored(int increment) throws NotConnectedException, IOException
    {
        guaranteePlayerConnection();
        
        rivalOutputStream.writeShort(pointsScored);
        rivalOutputStream.writeInt(increment);
        rivalOutputStream.flush();
    }
}
