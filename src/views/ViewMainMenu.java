package views;

import display.View;
import game.Game;
import game.Settings;

public class ViewMainMenu extends View {

    /** @see View#View(Settings) */
    public ViewMainMenu(View parrent,Settings settings) {
        super(parrent,settings);
        views.put("exitButton",new ViewButton(this,Game.renderer.getSettings("exitButton"), () -> {
            System.out.println("Exiting...");
            Game.running = false;
            Game.gameFrame.dispose();
            Game.gameFrame.disconnect();
            Game.gameFrame.stopServer();
        }));
        views.put("multiplayerButton",new ViewButton(this,Game.renderer.getSettings("multiplayerButton"), () -> {
            this.views.put("multiplayerMenu", new ViewMultiplayerMenu(this,Game.renderer.getSettings("multiplayerMenu")));
        }));
        
    }


}
