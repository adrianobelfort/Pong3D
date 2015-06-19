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
public interface PlayerSendingProtocol 
{
    public void sendCollision(float xPosition, float zPosition, float vx, float vz) throws IOException;
    public void sendRequestToPlay() throws IOException;
    public boolean sendResponseToPlay(boolean response) throws IOException;
    public void sendGameStart(float[] initialSpeeds) throws IOException;
    public void sendBlockMove(float xIncrement, float zIncrement) throws IOException;
    public void sendPointsScored(int pointsIncrement) throws IOException;
    public void sendDisconnectToPlayer() throws IOException;
}