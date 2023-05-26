package game;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import display.Renderer;

public class Settings {
    public String name;
    public Map<String, String> settings = new HashMap<String, String>();

    public Settings(String name) {
        this.name = name;
    }

    public Settings(String name, ObjectInputStream ois) {
        this.name = name;
        try {
            String nextString = (String) ois.readObject();
            while (nextString.equals("endView")) {
                settings.put(nextString, (String) ois.readObject());
                nextString = (String) ois.readObject();
            }

        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Error while loading settings: " + e);
        }

    }

    /**
     * @param pairArray contains "key","value","key","value".. of future
     *                  settings-Map
     */
    public Settings(String name, String[] pairArray) {
        this.name = name;
        for (int i = 0; i < pairArray.length; i += 2) {
            settings.put(pairArray[i], pairArray[i + 1]);
        }
    }

    public void writeToFile(ObjectOutputStream oos) {
        try {
            oos.writeObject(this.name);
            for (String s : settings.keySet()) {
                oos.writeObject(s);
                oos.writeObject(settings.get(s));
            }
            oos.writeObject("endView");
        } catch (IOException e) {
            System.out.println("Error while saving settings: " + e);
        }
    }

    public String get(String key) {
        return settings.get(key);
    }

    public String[] get(String[] keys) {
        String[] result = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            result[i] = settings.get(keys[i]);
        }
        return result;
    }

    public static void initSettings(Renderer renderer) {
        Map<String, Settings> settingsMap = new HashMap<String, Settings>();
        settingsMap.put("global", new Settings("global", new String[] {
                "leftclick", "LEFTCLICK", "toggleEditMode", "CIRCUMFLEX", "backspace", "BACKSPACE", "enter", "ENTER"
        }));
        settingsMap.put("editMode", new Settings("editMode", new String[] {
                "up-left", "Q", "up", "W", "up-right", "E", "left", "A", "center", "S", "right", "D", "down-left", "Y",
                "down", "X", "down-right", "C"
        }));
        settingsMap.put("editModeView",
                new Settings("editModeView", new String[] { "x1", "1520", "y1", "0", "x2", "1920", "y2", "100",
                        "background", "buttonBackground", "text", "EDITMODE", "textColor",
                        String.valueOf(Color.WHITE.getRGB()), "textAlignment", "CENTER",
                        "textFont", "BORDERED", "textSize", "28", "textBorder", "2", "textPosition", "CENTER",
                        "borderType", "DEFAULT", "borderWidth", "16" }));
        settingsMap.put("mainMenu",
                new Settings("mainMenu", new String[] { "x1", "0", "y1", "0", "x2", "1920", "y2", "1080",
                        "background", "mainMenuBackground", }));
        settingsMap.put("exitButton",
                new Settings("exitButton", new String[] { "x1", "1720", "y1", "880", "x2", "1920", "y2", "1080",
                        "background", "buttonBackground", "borderType", "DEFAULT", "borderWidth", "16", "text", "Exit",
                        "textColor",
                        String.valueOf(Color.WHITE.getRGB()), "textAlignment", "CENTER", "textFont",
                        "BORDERED", "textSize", "64", "textBorder", "2", "textPosition", "CENTER" }));
        settingsMap.put("world",
                new Settings("world", new String[] { "x1", "0", "y1", "0", "x2", "1920", "y2", "1080", "selectFirstPoint", "LEFTCLICK", "selectSecondPoint", "RIGHTCLICK", "select", "LEFTCLICK"
            , "cancelSelect", "RIGHTCLICK" }));
        settingsMap.put("multiplayerButton",
                new Settings("multiplayerButton", new String[] { "x1", "660", "y1", "400", "x2", "1260", "y2", "600",
                        "background", "buttonBackground", "borderType", "DEFAULT", "borderWidth", "16", "text",
                        "Multiplayer", "textColor", String.valueOf(Color.WHITE.getRGB()), "textAlignment", "CENTER",
                        "textFont", "BORDERED", "textSize", "64", "textBorder", "2", "textPosition", "CENTER" }));
        settingsMap.put("multiplayerMenu", new Settings("multiplayerMenu", new String[] { "x1", "300", "y1", "200", "x2",
                "1620", "y2", "880", "background", "buttonBackground", "borderType", "DEFAULT", "borderWidth", "16" }));
        settingsMap.put("ipInput", new Settings("ipInput", new String[] { "rX1", "0", "rY1", "0", "rX2", "600", "rY2",
                "150", "background", "textInput", "borderType", "BRIGHT", "borderWidth", "16", "text", "localhost",
                "textColor", String.valueOf(Color.WHITE.getRGB()), "textAlignment", "CENTER", "textFont", "BORDERED",
                "textSize", "48", "textBorder", "2", "textPosition", "CENTER" }));
        settingsMap.put("portInput", new Settings("portInput", new String[] { "rX1", "0", "rY1", "250", "rX2", "300",
                "rY2", "400", "background", "textInput", "borderType", "BRIGHT", "borderWidth", "16", "text",
                "8888", "textColor", String.valueOf(Color.WHITE.getRGB()), "textAlignment", "CENTER", "textFont",
                "BORDERED", "textSize", "64", "textBorder", "2", "textPosition", "CENTER" }));
        settingsMap.put("connectButton", new Settings("connectButton", new String[] { "rX1", "0", "rY1", "450", "rX2",
                "300", "rY2", "600", "background", "buttonBackground", "borderType", "DEFAULT", "borderWidth", "16",
                "text", "Connect", "textColor", String.valueOf(Color.WHITE.getRGB()), "textAlignment", "CENTER",
                "textFont", "BORDERED", "textSize", "32", "textBorder", "2", "textPosition", "CENTER" }));
        settingsMap.put("startServerButton", new Settings("startServerButton", new String[] { "rX1", "-300", "rY1", "-150",
                "rX2", "-0", "rY2", "-0", "background", "buttonBackground", "borderType", "DEFAULT", "borderWidth",
                "16", "text", "Start Server", "textColor", String.valueOf(Color.WHITE.getRGB()), "textAlignment",
                "CENTER", "textFont", "BORDERED", "textSize", "16", "textBorder", "2", "textPosition", "CENTER" }));
        renderer.settingsMap = settingsMap;

    }

    public static void init() {
        if (!Game.renderer.readSettings()) {
            Settings.initSettings(Game.renderer);
        }
    }
}
