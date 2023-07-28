package views;

import display.View;
import game.Game;
import game.Settings;

public class ViewServerManager extends View {

    public int port = 8888;
    public int edgeLimit = 4;
    public int edgeStoredLimit = 4;

    public ViewServerManager(View parrent, Settings settings) {
        super(parrent, settings);
        views.put("portInput", new ViewTextInput(this, Game.renderer.getSettings("portInput"), (String s) -> {
            try {
                port = Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println("Invalid port");
            }
        }));
        views.put("edgeLimitInput", new ViewTextInput(this, Game.renderer.getSettings("edgeLimitInput"), (String s) -> {
            try {
                edgeLimit = Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println("Invalid edgeLimit");
            }
        }));
        views.put("edgeStoredLimitInput", new ViewTextInput(this, Game.renderer.getSettings("edgeStoredLimitInput"), (String s) -> {
            try {
                edgeStoredLimit = Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println("Invalid edgeLimit");
            }
        }));
        views.put("startServerButton", new ViewButton(this, Game.renderer.getSettings("startServerButton"), () -> {
            System.out.println(parrent.views.keySet());
            remove();
            Game.gameFrame.startServer(new int[] { port, edgeLimit, edgeStoredLimit });
        }));
    }
}
