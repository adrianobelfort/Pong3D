/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalmenu;

import app.Pong;
import game.GameAgents;
import game.GameState;
import javax.swing.JFrame;
import multiplayer.GameClient;

/**
 *
 * @author Titanium
 */

public class Menu_janela extends JFrame{
   
    public static String serverIP;
    public static GameAgents newAgents;
    public static GameState newState;
    public static GameClient newMultiplayerHandler;
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        Janela janela = new Janela();
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
        //new Menu_janela();
        //janela.setSize(600,600);
        newAgents = new GameAgents();
        newState = new GameState();
//        newMultiplayerHandler = new GameClient(serverIP, newAgents, newState);
//        Thread multiplayerThread = new Thread(newMultiplayerHandler);
//        multiplayerThread.start();
        
        //Pong listener = new Pong(newAgents, newState, newMultiplayerHandler);
//        Pong.painter = new Pong(newAgents, newState, newMultiplayerHandler);
        //newState.bindPainter(listener);
        
        new Thread(Pong.painter).start();
                
    }
    
}
