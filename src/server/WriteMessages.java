package server;

import game.Player;

public class WriteMessages {
    
    public static void playersMessage(){
        String message = "GameData%";
        for(Player player : Server.players.values()){
            message += player.getMessage() + " ";
        }
        Server.sendDataToAll(message);
    }

}
