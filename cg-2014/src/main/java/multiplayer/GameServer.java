/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Adriano
 */
public class GameServer extends MultiplayerAgent implements Runnable
{     
    private final Socket clientHandlerSocket;
    
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(serverListeningPort);
        
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

    private GameServer(Socket clientSocket) 
    {
        clientHandlerSocket = clientSocket;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
