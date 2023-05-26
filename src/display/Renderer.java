package display;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import basis.FontHandler;
import basis.KeyHandler;
import game.Game;
import game.Settings;
import image_handler.ImageLoader;
import views.ViewMainMenu;
import views.ViewText;

public class Renderer {

    public ArrayList<View> views = new ArrayList<View>();
    public int activeViews = 0;
    public Mode mode = Mode.INTERACTIVE;
    public View selectedView = null;
    public Map<String,Settings> settingsMap = new HashMap<String, Settings>();

    public Renderer() {
        Game.renderer = this;
        ImageLoader.init();
        FontHandler.init();
        Settings.init();
        views.add(new ViewMainMenu(null,getSettings("mainMenu")));
        views.add(new ViewText(null,getSettings("editModeView")));
        views.get(1).active = Mode.EDIT == mode;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, 1920, 1080);
        // Draw Grid
        g.setColor(new Color(0,0,0,50));
        for (int i = 0; i < 1920; i += 60) {
            g.drawLine(i, 0, i, 1080);
        }
        for (int i = 0; i < 1080; i += 60) {
            g.drawLine(0, i, 1920, i);
        }
        activeViews = 0;
        for (View v : views) {
            if (v.active) {
                activeViews++;
                v.draw(g);
            }
        }
    }

    public void handleInputs() {
        KeyHandler.tick();

        Settings globalSettings = getSettings("global");

        if (KeyHandler.consumeKey(globalSettings.get("toggleEditMode"))) {
            mode = mode == Mode.EDIT ? Mode.INTERACTIVE : Mode.EDIT;
            views.get(1).active = mode == Mode.EDIT;
            
        }
        switch (mode) {
            case INTERACTIVE:
                for (View v : views) {
                    if (v.active)
                        v.handleInputs();
                }
                break;
            case EDIT:

                if(KeyHandler.consumeKey(globalSettings.get("leftclick"))) {
                    selectedView = selectOnCursor();
                }
                
                for (View v : views) {
                    if (v.active &&  selectedView != null)
                        selectedView.handleEditorInputs();
                }
                break;
        }

    }

    public View selectOnCursor(){
        
                View result = null;
                for(View v : views){
                    View subResult = v.selectOnCursor();
                    if(subResult != null)
                        result = subResult;
                }
                return result;
    }

    /** Retrieves Settings from File */
    public boolean readSettings(){
        //Retrieve settings from file
        try {
            
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("settings"));
            String nextString = (String) ois.readObject();
            while(!nextString.equals("endFile")){
                settingsMap.put(nextString, new Settings(nextString, ois));
                nextString = (String) ois.readObject();
            }
            ois.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Writes all Settings into a file */
    public void writeAllSettings(){
        // Write settings to file
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new java.io.FileOutputStream("settings"));
            for(Settings s : settingsMap.values()){
                s.writeToFile(oos);
            }
            oos.writeObject("endFile");
            oos.close();
        } catch (Exception e) {
        }
    }

    enum Mode {
        INTERACTIVE, EDIT
    }

    public Settings getSettings(String s){
        Settings result = settingsMap.get(s);
        if(result != null)
            return result;
        return new Settings(s);

    }
}
