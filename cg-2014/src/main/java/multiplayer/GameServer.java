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
import java.util.LinkedHashMap;

/**
 *
 * @author Adriano
 */
public class GameServer extends MultiplayerAgent implements Runnable
{     
    private final Socket clientHandlerSocket;
    private static LinkedHashMap players;
    private static LinkedHashMap availabilityMap;
    private String playerNickname;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    // TODO: Create a new class that holds the nickname, ip address and availability to represent a player
    
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(serverListeningPort);
        players = new LinkedHashMap();
        availabilityMap = new LinkedHashMap();
        
        System.out.println("Server initialized on port " + serverListeningPort);
        
        // Run forever, accepting and spawning threads to service each connection
        while(true)
        {
            Socket clientSocket = null;
            
            try
            {
                clientSocket = serverSocket.accept();
                System.out.println("Server connected to " + clientSocket.getInetAddress().getHostAddress());
            
                GameServer serverHandler = new GameServer(clientSocket);
                Thread serverThread = new Thread(serverHandler);
                serverThread.start();
            }
            catch (IOException e)
            {
                System.out.println("IO Exception: " + e.getMessage());
            }
        }
    }

    private GameServer(Socket clientSocket) throws IOException 
    {
        clientHandlerSocket = clientSocket;
        
        inputStream = new DataInputStream(new BufferedInputStream(clientHandlerSocket.getInputStream()));
        outputStream = new DataOutputStream(new BufferedOutputStream(clientHandlerSocket.getOutputStream()));
    }
    
    public synchronized boolean addPlayer(String nickname)
    {
        if(players.putIfAbsent(nickname, clientHandlerSocket.getInetAddress().getHostAddress()) == null)
        {
            playerNickname = nickname;
            availabilityMap.putIfAbsent(nickname, true);
            return true;
        }
        else
        {
            return false;   
        }
    }
    
    public synchronized void removePlayer()
    {
        players.remove(playerNickname);
    }
    
    public synchronized void changeAvailability(boolean availabilityState)
    {
        availabilityMap.replace(playerNickname, availabilityState);
    }
    
    public synchronized void sendListOfPlayers() throws IOException
    {
        outputStream.writeShort(sendingPlayersList);
        outputStream.writeInt(players.size());
        //for (String nickname : players) continue looking here
    }

    @Override
    public void run() 
    {
        // This method will receive requests and handle them, calling other helper methods
        // Basically, it should work like a main method
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
