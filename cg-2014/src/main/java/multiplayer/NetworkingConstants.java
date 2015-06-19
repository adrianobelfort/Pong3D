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
public interface NetworkingConstants 
{
    // Ports
    public static final int serverListeningPort = 3020;
    public static final int playerListeningPort = 3021;
    
    // Player codes
    public static final short ballCollisionCode = 1;
    public static final short moveBlockCode = 2;
    public static final short invitationToPlayCode = 3;
    public static final short responseToInvitationToPlayCode = 4;
    public static final short scoredPointsCode = 5;
    public static final short gameStartCode = 6;
    public static final short closeConnectionCode = 7;
    
    // Server codes
    public static final short changeAvailabilityCode = 8;
    public static final short requestPlayersListCode = 9;
    public static final short sendPlayersListCode = 10;
    public static final short sendNicknameCode = 11;
    public static final short nicknameTransactionCode = 12;
    public static final short unregisterPlayerCode = 13;
    public static final short shutdownServerCode = 14;
}