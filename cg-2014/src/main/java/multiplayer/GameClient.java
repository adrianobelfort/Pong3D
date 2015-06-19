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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adriano
 */
public class GameClient implements Runnable, PlayerSendingProtocol, ServerSendingProtocol, NetworkingConstants
{    
    private Socket playerToServerSocket, playerToPlayerSocket;
    private ServerSocket incomingConnectionsSocket;
    private DataInputStream serverInput = null, playerInput; 
    private DataOutputStream serverOutput = null, playerOutput;
    private String nickname, rivalNickname;
    private boolean playerAvailability;
    
    private static int rivalListeningPort;
    private static int myListeningPort;
    
    private static Scanner scanner;
    
    private final TreeMap<String, PlayerInfo> players;
    
    GameClient()
    {
        playerAvailability = true;
        players = new TreeMap<>();
    }
    
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
        
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException 
    {
        scanner = new Scanner(System.in);
        
        System.out.print("Enter the port at which YOU will be listening: ");
        myListeningPort = scanner.nextInt();
        scanner.nextLine();
        
        new Thread(new GameClient()).start();
    }
    
    public Thread acceptConnections(final int listeningPort)
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Socket establishedConnectionSocket;
                
                try 
                {
                    // Line below was commented because the socket is initialized on run, before calling acceptConnections
                    // It may be the case that it will become necessary to uncomment this line
                    //incomingConnectionsSocket = new ServerSocket(myListeningPort/*listeningPort*/);
                    
                    System.out.println("Waiting for connections...\n");
                    establishedConnectionSocket = incomingConnectionsSocket.accept();
                    
                    if(playerToPlayerSocket == null || playerToPlayerSocket.isClosed())
                    {
                        playerToPlayerSocket = establishedConnectionSocket;
                        playerOutput = new DataOutputStream(new BufferedOutputStream(playerToPlayerSocket.getOutputStream()));
                        denyConnections();
                        
                        System.out.println("Player connected to player at " + playerToPlayerSocket.getInetAddress().getHostAddress() + ", port " + playerToPlayerSocket.getPort());
                    }
                    else
                    {
                        establishedConnectionSocket.close();
                    }
                    
                    new Thread(new PlayerListener(playerToPlayerSocket)).start();
                } 
                catch (SocketException sex)
                {
                    //System.out.println("Client is not accepting new incoming connection requests anymore");
                }
                catch (IOException ex) 
                {
                    System.out.println("Client is unable to listen at port " + listeningPort);
                }
            }
        });
        t.start();
        return t;
    }
    
    public void denyConnections()
    {
        try 
        {
            incomingConnectionsSocket.close();
            System.out.println("[Player is connected to another player: incoming connections are now disabled]");
        } 
        catch (IOException ex) 
        {
            System.out.println("Exception when attempting to close socket");
        }
    }
    
    public void connectToPlayer(String playerIP/*, String nickname*/) throws IOException
    {       
        System.out.println("Connecting to player " + nickname + " ("+playerIP+") at port " + rivalListeningPort/*playerListeningPort*/);
        
        playerToPlayerSocket = new Socket(playerIP, rivalListeningPort/*playerListeningPort*/);
        playerToPlayerSocket.setTcpNoDelay(true);
        
        playerOutput = new DataOutputStream(new BufferedOutputStream(playerToPlayerSocket.getOutputStream()));
        
        if (incomingConnectionsSocket != null && !incomingConnectionsSocket.isClosed())
        {
            denyConnections();
        }
        
        // Beware here!
        new Thread(new PlayerListener(playerToPlayerSocket)).start();
    }
    
    public void disconnectFromPlayer() throws IOException
    {
        if (playerToPlayerSocket != null && !playerToPlayerSocket.isClosed())
        {
            playerInput.close();
            playerOutput.close();
            playerToPlayerSocket.close();
        }
        changeAvailability(true);
    }
    
    @Override
    public void connectToServer(String serverIP, int serverPort) throws IOException
    {
        playerToServerSocket = new Socket(serverIP, serverPort);
        serverOutput = new DataOutputStream(new BufferedOutputStream(playerToServerSocket.getOutputStream()));
        new Thread(new ServerListener(playerToServerSocket)).start();
    }
    
    @Override
    public void disconnectFromServer() throws IOException
    {
        if (playerToServerSocket != null && !playerToServerSocket.isClosed())
        {
            serverInput.close();
            serverOutput.close();
            playerToServerSocket.close();
        }
    }
    
    public static void playerOptions()
    {
        System.out.println("Options:");
        System.out.println("\tCollision, connect, disconnect, accept connections, invite, accept invitation, reject invitation,");
        System.out.println("\tmove, score, game start");
    }
    
    public static void serverOptions()
    {
        System.out.println("Options:");
        System.out.println("\tConnect, availability, players list, send nickname, unregister, disconnect, shutdown");
    }
    
    @Override
    public void run()
    {
        String destination, action;
        //String stringToSend, stringToReceive;
        String serverIP, rivalIP;
        String mNick, rNick;
        int serverPort;
        
        try 
        {
            playerToPlayerSocket = null;
            
            // You can move this part to the constructor when implementing the actual game
            System.out.print("Enter the address of the server: ");
            serverIP = scanner.nextLine();
            serverPort = serverListeningPort;
            
            System.out.println("Connecting to " + serverIP + " at port " + serverPort + "...");
            try
            {
                connectToServer(serverIP, serverPort);
                System.out.println("Connection successfully established.\n");
            }
            catch (IOException ex)
            {
                System.out.println("Unable to connect to server.");
            }
            
            // It's better to move this line to the constructor later...
            incomingConnectionsSocket = new ServerSocket(myListeningPort/*listeningPort*/);
            // maybe comment this later - maybe not
            acceptConnections(myListeningPort);
            
            
            do
            {                
                System.out.print("Specify the destination: ");
                destination = scanner.nextLine();
                
                if (destination.equalsIgnoreCase("player"))
                {
                    playerOptions();
                    System.out.print("What would you like to do? ");
                    action = scanner.nextLine();
                    
                    if (action.equalsIgnoreCase("connect"))
                    {                        
                        System.out.print("Enter the nickname of the player to which you want to connect: ");
                        rNick = scanner.nextLine();
                        PlayerInfo player = players.get(rNick);
                        
                        if (player == null)
                        {
                            System.out.println("Player " + rNick + " is not in the lobby");
                            continue;
                        }
                        
                        rivalIP = player.IP;
                        rivalListeningPort = player.listeningPort;

                        try
                        {
                            connectToPlayer(rivalIP);
                        }
                        catch (IOException ex)
                        {
                            System.out.println("Unable to connect to player at " + rivalIP);
                        }
                        finally
                        {
                            System.out.println("Connection between players successfully established.\n");
                        }
                    }
                    else if (action.equalsIgnoreCase("collision"))
                    {
                        float x, z, vx, vz;
                        
                        System.out.print("Enter the x position of the ball: ");
                        x = scanner.nextFloat();
                        System.out.print("Enter the z position of the ball: ");
                        z = scanner.nextFloat();
                        System.out.print("Enter the x speed of the ball: ");
                        vx = scanner.nextFloat();
                        System.out.print("Enter the z speed of the ball: ");
                        vz = scanner.nextFloat();
                        
                        // just for correctness
                        scanner.nextLine();
                        
                        sendCollision(x, z, vx, vz);
                    }
                    else if (action.equalsIgnoreCase("disconnect"))
                    {
                        disconnectFromPlayer();
                        changeAvailability(true);
                        //acceptConnections(playerListeningPort);
                    }
                    else if (action.equalsIgnoreCase("accept connections"))
                    {
                        acceptConnections(myListeningPort);
                    }
                    else if (action.equalsIgnoreCase("invite"))
                    {
                        sendRequestToPlay();
                    }
                    else if (action.equalsIgnoreCase("accept invitation"))
                    {
                        sendResponseToPlay(true);
                        changeAvailability(false);
                    }
                    else if (action.equalsIgnoreCase("reject invitation"))
                    {
                        sendResponseToPlay(false);
                    }
                    else if (action.equalsIgnoreCase("move"))
                    {
                        float xinc, zinc;
                        
                        System.out.print("Enter the increment on X: ");
                        xinc = scanner.nextFloat();
                        System.out.print("Enter the increment on Z: ");
                        zinc = scanner.nextFloat();
                        scanner.nextLine();
                        
                        sendBlockMove(xinc, zinc);
                    }
                    else if (action.equalsIgnoreCase("score"))
                    {
                        int sinc;
                        
                        System.out.print("What's the score increment? ");
                        sinc = scanner.nextInt();
                        scanner.nextLine();
                        
                        sendPointsScored(sinc);
                    }
                    else if (action.equalsIgnoreCase("game start"))
                    {
                        float[] speeds = new float[2];
                        
                        System.out.print("Enter the x speed of the ball: ");
                        speeds[0] = scanner.nextFloat();
                        System.out.print("Enter the z speed of the ball: ");
                        speeds[1] = scanner.nextFloat();
                        scanner.nextLine();
                        
                        sendGameStart(speeds);
                    }
                    else
                    {
                        System.out.println("Sorry, I could not understand what you mean");
                    }
                }
                else if (destination.equalsIgnoreCase("server"))
                {
                    serverOptions();
                    
                    System.out.print("What would you like to do? ");
                    action = scanner.nextLine();
                    
                    if (action.equalsIgnoreCase("connect"))
                    {
                        System.out.println("Connecting to server...");
                        connectToServer(serverIP, serverPort);
                        System.out.println("Done.");
                    }
                    else if (action.equalsIgnoreCase("availability"))
                    {
                        System.out.print("Are you available? ");
                        action = scanner.nextLine();
                        if (action.equalsIgnoreCase("yes"))
                        {
                            requestAvailabilityChange(true);
                        }
                        else if (action.equalsIgnoreCase("no"))
                        {
                            requestAvailabilityChange(false);
                        }
                        else
                        {
                            System.out.println("I could not understand your \"" + action + "\", try again!");
                        }
                    }
                    else if (action.equalsIgnoreCase("players list"))
                    {
                        System.out.println("Requesting the players' list...");
                        requestListOfPlayers();
                    }
                    else if (action.equalsIgnoreCase("send nickname"))
                    {
                        System.out.print("Insert a nickname for you: ");
                        mNick = scanner.nextLine();
                        
                        requestToRegisterPlayer(mNick, myListeningPort);
                    }
                    else if (action.equalsIgnoreCase("disconnect"))
                    {
                        disconnectFromServer();
                    }
                    else if (action.equalsIgnoreCase("unregister"))
                    {
                        requestToUnregisterPlayer();
                    }
                    else if (action.equalsIgnoreCase("shutdown"))
                    {
                        sendServerShutdown();
                    }
                    else
                    {
                        System.out.println("Sorry, I could not understand what you mean");
                    }
                }
                
            } while(!destination.equalsIgnoreCase("exit"));
            
            System.out.println("Closing all connections...\n");
            denyConnections();
            disconnectFromPlayer();
            disconnectFromServer();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // PLAYER METHODS
    
    @Override
    public void sendCollision(float xPosition, float zPosition, float vx, float vz) throws IOException
    {
        playerOutput.writeShort(ballCollisionCode);
        playerOutput.writeFloat(xPosition);
        playerOutput.writeFloat(zPosition);
        playerOutput.writeFloat(vx);
        playerOutput.writeFloat(vz);
        playerOutput.flush();
    }

    @Override
    public void sendRequestToPlay() throws IOException 
    {
        playerOutput.writeShort(invitationToPlayCode);
        playerOutput.writeUTF(nickname);
        playerOutput.flush();
    }

    @Override
    public boolean sendResponseToPlay(boolean response) throws IOException 
    {
        boolean returnValue;
        
        playerOutput.writeShort(responseToInvitationToPlayCode);
        playerOutput.writeUTF(nickname);
        if (playerAvailability == false)
        {
            playerOutput.writeBoolean(false);
            returnValue = false;    // false means the match was denied anyway
        }
        else
        {
            playerOutput.writeBoolean(response);
            returnValue = true;     // true means the player's wish was respected
        }
        playerOutput.writeBoolean(playerAvailability);
        playerOutput.flush();
        
        return returnValue;
    }

    @Override
    public void sendGameStart(float[] initialSpeeds) throws IOException 
    {
        playerOutput.writeShort(gameStartCode);
        playerOutput.writeFloat(initialSpeeds[0]);
        playerOutput.writeFloat(initialSpeeds[1]);
        playerOutput.flush();
    }

    @Override
    public void sendBlockMove(float xIncrement, float zIncrement) throws IOException 
    {
        playerOutput.writeShort(moveBlockCode);
        playerOutput.writeFloat(xIncrement);
        playerOutput.writeFloat(zIncrement);
        playerOutput.flush();
    }

    @Override
    public void sendPointsScored(int pointsIncrement) throws IOException 
    {
        playerOutput.writeShort(scoredPointsCode);
        playerOutput.writeInt(pointsIncrement);
        playerOutput.flush();
    }
    
    @Override
    public void sendDisconnectToPlayer() throws IOException 
    {
        playerOutput.writeShort(closeConnectionCode);
        playerOutput.flush();
    }
    
    // SERVER METHODS
    
    public void changeAvailability(boolean newAvailability) throws IOException
    {
        playerAvailability = newAvailability;
        requestAvailabilityChange(newAvailability);
    }

    @Override
    public void requestAvailabilityChange(boolean availability) throws IOException 
    {
        serverOutput.writeShort(changeAvailabilityCode);
        serverOutput.writeBoolean(availability);
        serverOutput.flush();
    }

    @Override
    public void requestToRegisterPlayer(String newNickname, int invitationPort) throws IOException 
    {
        nickname = newNickname;
        
        serverOutput.writeShort(sendNicknameCode);
        serverOutput.writeUTF(newNickname);
        serverOutput.writeInt(invitationPort);
        serverOutput.flush();
    }

    @Override
    public void requestListOfPlayers() throws IOException 
    {
        serverOutput.writeShort(requestPlayersListCode);
        serverOutput.flush();
    }

    @Override
    public void requestToUnregisterPlayer() throws IOException 
    {
        // send to SERVER
        serverOutput.writeShort(unregisterPlayerCode);
        serverOutput.flush();
    }

    @Override
    public void sendDisconnectToServer() throws IOException 
    {
        serverOutput.writeShort(closeConnectionCode);
        serverOutput.flush();
    }

    @Override
    public void sendServerShutdown() throws IOException 
    {
        serverOutput.writeShort(shutdownServerCode);
        serverOutput.flush();
    }

    public class PlayerListener implements Runnable, PlayerReceivingProtocol
    {
        private final Socket clientSocket;
        //private final DataInputStream playerInput;

        PlayerListener(Socket newSocket) throws IOException
        {
            clientSocket = newSocket;

            playerInput = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        }

        @Override
        public void run() 
        {
            boolean running = true;
            boolean safeToProceed = true;
            short opcode;
            
            System.out.println("[Player listener is ON]");
            
            while(running && safeToProceed)
            {
                try 
                {
                    opcode = playerInput.readShort();
                    
                    switch(opcode)
                    {                        
                        case ballCollisionCode:
                            receiveCollision();
                        break;
                            
                        case moveBlockCode:
                            receiveBlockMove();
                        break;
                            
                        case invitationToPlayCode:
                            receiveRequestToPlay();
                        break;
                            
                        case responseToInvitationToPlayCode:
                            // do not forget to check availability and cite that in the message!
                            if (receiveResponseToPlay())
                            {
                                changeAvailability(false);
                            }
                        break;
                            
                        case scoredPointsCode:
                            receivePointsScored();
                        break;
                            
                        case gameStartCode:
                            receiveGameStart();
                        break;
                            
                        case closeConnectionCode:
                            running = false;
                            receiveDisconnectFromPlayer();
                            changeAvailability(true);
                        break;
                            
                        default:
                            System.out.println("Sorry, I could not understand. You sent an opcode of " + opcode + ".");
                    }
                } 
                catch (SocketException | EOFException aex)
                {
                    safeToProceed = false;
                    //System.out.println("An EOF or Socket exception happened. Check the stack:");
                    //Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, aex);
                }
                catch (IOException ex) 
                {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            System.out.println("[Player listener is OFF]");
        }

        @Override
        public void receiveCollision() throws IOException
        {
            float xPosition, zPosition, vx, vz;

            xPosition = playerInput.readFloat();
            zPosition = playerInput.readFloat();
            vx = playerInput.readFloat();
            vz = playerInput.readFloat();

            System.out.println("\nCollision detected!\n\tBall position: ("+xPosition+ ", " +zPosition+")");
            System.out.println("\tSpeed: (" + vx + ", " + vz + ")");
        }

        @Override
        public void receiveRequestToPlay() throws IOException 
        {
            String opponentNickname;
            
            opponentNickname = playerInput.readUTF();
            rivalNickname = opponentNickname;
            
            System.out.println(opponentNickname + " requested to play with you");
        }

        @Override
        public boolean receiveResponseToPlay() throws IOException 
        {
            boolean response, rivalAvailability;
            String rNick;
            
            rNick = playerInput.readUTF();
            response = playerInput.readBoolean();
            rivalAvailability = playerInput.readBoolean();
            
            rivalNickname = rNick;
            
            // Very important!
            if (rivalAvailability == false)
            {
                requestListOfPlayers();
            }
            
            if (response == true)
            {
                System.out.println(rivalNickname + " accepted your request to play");
            }
            else if (response == false && rivalAvailability == true)
            {
                System.out.println(rivalNickname + " did not accept your request.");
            }
            else
            {
                System.out.println(rivalNickname + " could not accept your request because they are not available");
            }
            
            return response;
        }

        @Override
        public void receiveGameStart() throws IOException 
        {
            float[] initialSpeeds = new float[2];
            
            initialSpeeds[0] = playerInput.readFloat();
            initialSpeeds[1] = playerInput.readFloat();
            
            System.out.println("Game starts with ball moving towards ("+initialSpeeds[0]+", "+initialSpeeds[1]+")");
        }

        @Override
        public void receiveBlockMove() throws IOException 
        {
            float xIncrement, zIncrement;
            
            xIncrement = playerInput.readFloat();
            zIncrement = playerInput.readFloat();
            
            System.out.println("Block is shifted by ("+xIncrement+", "+zIncrement+")");
        }

        @Override
        public void receivePointsScored() throws IOException 
        {
            int points;
            
            points = playerInput.readInt();
            System.out.print("Scored " + points + " point");
            if (points > 1)
            {
                System.out.println("s");
            }
            else
            {
                System.out.println();
            }
        }

        @Override
        public void receiveDisconnectFromPlayer() throws IOException 
        {
            playerInput.close();
            clientSocket.close();
        }
    }
    
    public class ServerListener implements Runnable
    {
        private final Socket serverSocket;
        //private final DataInputStream serverInput;

        ServerListener(Socket newSocket) throws IOException
        {
            serverSocket = newSocket;
            serverInput = new DataInputStream(new BufferedInputStream(serverSocket.getInputStream()));
        }
        
        //@Override
        public boolean nicknameAccepted() throws IOException 
        {
            boolean response;
            response = serverInput.readBoolean();
            return response;
        }
        
        public void retrievePlayersList() throws IOException
        {
            int entries;
            String receivedNickname, receivedIP;
            int receivedPort;
            boolean receivedAvailability;
            PlayerInfo receivedInfo;
            
            synchronized(players)
            {
                entries = serverInput.readInt();
                
                for (int i = 0; i < entries; i++)
                {
                    receivedNickname = serverInput.readUTF();
                    receivedIP = serverInput.readUTF();
                    receivedPort = serverInput.readInt();
                    receivedAvailability = serverInput.readBoolean();
                    
                    receivedInfo = new PlayerInfo(receivedNickname, receivedIP, receivedPort, receivedAvailability);
                    
                    if (players.containsKey(receivedNickname))
                    {
                        players.replace(receivedNickname, receivedInfo);
                    }
                    else
                    {
                        players.put(receivedNickname, receivedInfo);
                    }
                }
            }
        }
        
        @Override
        public void run() 
        {
            System.out.println("[Server listener is ON]");
            
            boolean running = true;
            boolean safeToProceed = true;
            short opcode;
            
            while(running && safeToProceed)
            {
                try 
                {
                    opcode = serverInput.readShort();
                    
                    switch(opcode)
                    {                   
                        case requestPlayersListCode:
                            retrievePlayersList();
                        break;
                            
                        case closeConnectionCode:
                            running = false;
                            serverInput.close();
                            serverSocket.close();
                        break;
                            
                        case nicknameTransactionCode:
                            if(!nicknameAccepted())
                            {
                                System.out.println("The nickname " + nickname + " was not accepted. Try again.");
                            }
                            else
                            {
                                System.out.println("The nickname " + nickname + " was accepted.");
                            }
                        break;
                            
                        case sendPlayersListCode:
                            retrievePlayersList();
                            printPlayersList();
                        break;
                            
                        default:
                            System.out.println("Sorry, I could not understand. You sent an opcode of " + opcode + ".");
                    }
                } 
                catch (SocketException | EOFException aex)
                {
                    safeToProceed = false;
                    //System.out.println("An EOF or Socket exception happened. Check the log:");
                    //Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, aex);
                }
                catch (IOException ex) 
                {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            System.out.println("[Server listener is OFF]");
        }
    }
}