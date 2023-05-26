package views;

import display.View;
import game.Game;
import game.Settings;
import server.Client;

public class ViewMultiplayerMenu extends View {

    public int port = 8888;
    public String ip = "idolfan.ddns.net";

    public ViewMultiplayerMenu(View parrent,Settings settings) {
        super(parrent,settings);
        views.put("ipInput", new ViewTextInput(this,Game.renderer.getSettings("ipInput"), (String s) -> {
            ip = s;
        }));
        views.put("portInput", new ViewTextInput(this,Game.renderer.getSettings("portInput"), (String s) -> {
            try {
                port = Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println("Invalid port");
            }
        }));
        views.put("connectButton", new ViewButton(this,Game.renderer.getSettings("connectButton"), () -> {
            System.out.println("Trying to connect");
            if(Game.gameFrame.connect(ip, port) != null){
                System.out.println("Connected");
                Game.renderer.views.set(0,new ViewWorld(null, Game.renderer.getSettings("world"), Client.world));
            }
        }));
        views.put("startServerButton", new ViewButton(this,Game.renderer.getSettings("startServerButton"), () -> {
            Game.gameFrame.startServer(port);
        }));
    }

}
