/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplayer;

/**
 *
 * @author Adriano
 */
public interface NetworkingConstants 
{
    public static final int serverListeningPort = 3020;
    public static final int playerListeningPort = 3021;
    public static final int playerWritingPort = 3022;
    
    public static final short changingAvailability = 1;
    public static final short ballCollision = 2;
    public static final short movingBlock = 3;
    public static final short requestingPlayersList = 4;
    public static final short sendingPlayersList = 5;
    public static final short invitationToPlay = 6;
    public static final short responseToInvitationToPlay = 7;
    public static final short pointsScored = 8;
    public static final short gameStarts = 9;
}
