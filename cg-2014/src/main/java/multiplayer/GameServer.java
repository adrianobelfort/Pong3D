package multiplayer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adriano
 */
public class GameServer implements Runnable, NetworkingConstants, ServerReceivingProtocol
{
    private final Socket clientSocket;
    private static ServerSocket serverSocket;
    private static int runningClients = 0;
    private static boolean running = true;
    private final DataInputStream input;
    private final DataOutputStream output;
    private String playerNickname = "";
    
    private final static TreeMap<String, PlayerInfo> players = new TreeMap<>();
    
    public void printPlayersList()
    {
        System.out.println("List of players:");
        
        if (players.size() == 0)
        {
            System.out.println("\tNo players in the lobby");
        }
        else
        {
            for (PlayerInfo player : players.values())
            {
                System.out.print("\t"+player.nickname+ " (" + player.IP + ": " + player.listeningPort + "): ");
                if (player.availability)
                {
                    System.out.println("AVAILABLE");
                }
                else
                {
                    System.out.println("NOT AVAILABLE");
                }
            }
        }
        System.out.println();
    }
    
    public static void main(String args[]) throws IOException
    {
        int serverPort;
        Socket accSocket;
        
        serverPort = serverListeningPort;
        serverSocket = new ServerSocket(serverPort);
        System.out.println("Server started at address " + serverSocket.getInetAddress().getHostAddress() + ", port " + serverPort + "\n");
        
        while(running)
        {
            try
            {
                accSocket = serverSocket.accept();
                GameServer handler = new GameServer(accSocket);
                new Thread(handler).start();
            }
            catch(SocketException ex)
            {
                System.out.println("Incoming connections socket closed");
            }
        }
    }
    
    GameServer(Socket newSocket) throws IOException
    {
        clientSocket = newSocket;
        
        input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
    }

    @Override
    public void run() 
    {
        boolean safeToProceed = true;
        boolean threadRunning = true;
        short opcode;
        
        runningClients++;
        
        //String receivedMessage = "";
        
        System.out.println("Handling client at " + clientSocket.getInetAddress().getHostAddress() + " at port " + clientSocket.getPort());
        
        /*try 
        {
            // First, the server will receive the nickname of the player
            // and register to its player table
            while(!registerNickname())
            {
                notifyInvalidNickname();
                notifyAcceptedNickname();
            }
        } 
        catch (IOException ex) 
        {
            System.out.println("Could not register player due to an exception");
            try { clientSocket.close(); } catch (IOException ex1) { System.out.println("Exception on socket closing");}
            //System.exit(2);
            return;
        }*/
        
        // Echo mode
        /*receivedMessage = input.readUTF();
        System.out.println("\n[Message from client " + clientSocket.getInetAddress().getHostAddress() + "] " + receivedMessage);
        output.writeUTF(receivedMessage);
        output.flush();*/
        while(threadRunning && safeToProceed)
        {
            try 
            {
                opcode = input.readShort();

                switch(opcode)
                {
                    case changeAvailabilityCode:
                        System.out.println(playerNickname + " requested availability change");
                        receiveAvailabilityChange();
                    break;

                    case requestPlayersListCode:
                        System.out.println(playerNickname + " requested the list of players");
                        sendPlayersList();
                    break;
                        
                    case sendNicknameCode:
                        if (!registerNickname())
                        {
                            System.out.println("Invalid nickname: " + playerNickname);
                            notifyInvalidNickname();
                        }
                        else
                        {
                            notifyAcceptedNickname();
                            printPlayersList();
                        }
                    break;
                        
                    case unregisterPlayerCode:
                        unregisterNickname();
                    break;

                    case closeConnectionCode:
                        threadRunning = false;
                        receiveDisconnectFromPlayer();
                        unregisterNickname();
                    break;
                        
                    case shutdownServerCode:
                        receiveServerShutdown();
                    default:
                        System.out.println("Sorry, I could not understand [invalid action]");
                }
            } 
            catch (SocketException | EOFException aex)
            {
                safeToProceed = false;
            }
            catch (IOException ex) 
            {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try 
        {
            if (--runningClients == 0) 
            {
                System.out.println("No more clients to service. Shutting down...");
                receiveServerShutdown();
            }
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void receiveAvailabilityChange() throws SocketException, IOException 
    {
        boolean newAvailability = input.readBoolean();
        PlayerInfo player;
        
        synchronized(players)
        {
            player = players.get(playerNickname);
            if (player != null)
            {
                player.changeAvailability(newAvailability);
                players.replace(playerNickname, player);
                printPlayersList();
            }
        }
    }

    @Override
    public boolean registerNickname() throws SocketException, IOException
    {
        String chosenNickname = input.readUTF();
        playerNickname = chosenNickname;
        int pListeningPort = input.readInt();
        PlayerInfo tentativeNewPlayer = new PlayerInfo(chosenNickname, clientSocket.getInetAddress().getHostAddress(), pListeningPort, true);
        
        synchronized(players)
        {
            if (players.putIfAbsent(chosenNickname, tentativeNewPlayer) != null)
            {
                return false;
            }
        }
        
        playerNickname = chosenNickname;
        
        return true;
    }
    
    @Override
    public void unregisterNickname()
    {
        synchronized(players)
        {
            if (players.containsKey(playerNickname))
            {
                players.remove(playerNickname);
                System.out.println(playerNickname + " left the lobby");
            }
        }
    }

    @Override
    public void notifyInvalidNickname() throws IOException 
    {
        output.writeShort(nicknameTransactionCode);
        output.writeBoolean(false);
        output.flush();
    }

    @Override
    public void notifyAcceptedNickname() throws IOException 
    {
        output.writeShort(nicknameTransactionCode);
        output.writeBoolean(true);
        output.flush();
    }

    @Override
    public void sendPlayersList() throws IOException 
    {
        output.writeShort(sendPlayersListCode);
        synchronized(players)
        {
            output.writeInt(players.size());
            for (PlayerInfo playerInfo : players.values())
            {
                output.writeUTF(playerInfo.nickname);
                output.writeUTF(playerInfo.IP);
                output.writeInt(playerInfo.listeningPort);
                output.writeBoolean(playerInfo.availability);
            }
        }
        output.flush();
    }

    @Override
    public void receiveDisconnectFromPlayer() throws IOException 
    {
        unregisterNickname();
        input.close();
        clientSocket.close();
    }

    @Override
    public void receiveServerShutdown() throws IOException 
    {
        running = false;
        serverSocket.close();    
    }
}