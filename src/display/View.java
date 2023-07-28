package display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import basis.KeyHandler;
import basis.MouseMotionListener;
import display.Border.BorderType;
import game.Game;
import game.Settings;
import image_handler.ImageHandler;
import image_handler.ImageLoader;

public class View {

    /** Only active Views are displayed and handle Inputs */
    public boolean active = true;
    /** "left" x position */
    private int x1 = 0;
    /** "upper" y position */
    private int y1 = 0;
    private int width = 0;
    private int height = 0;
    /** "right" x position */
    private int x2 = 0;
    /** "bottom" y position */
    private int y2 = 0;

    public String bgImageRef = "BLACK";
    /** Final Corresponding Image to bgImageRef. */
    private final Image baseImage;
    /** Adjustable Image which will be drawn */
    public Image image;

    /**
     * The border "surrounding" the view. However, the border will be drawn inside
     * the width and height of the view, resulting in the shrinking of the
     * background image.
     */
    public Border border;

    /** sub-Views "infront" of this */
    public Map<String, View> views = new HashMap<>();

    public ArrayList<View> viewsList = new ArrayList<>();

    /** Key of Settings inside renderer.settingsMap */
    public String settingsName;

    public Settings settings;

    public boolean canBeScrolled;

    public double scrollSpeed;

    public View parrent;

    public static Settings defaultSettings = new Settings("defaults", new String[] { "x1", "0", "x2", "1920", "y1", "0",
            "y2", "1080", "borderType", "NONE", "borderWidth", "0", "bgImageRef", "empty" });

    /**
     * Relative Positions use parrent.x1/y1 when positive, and parrent.x2/y2 when negative.
     * @param settings supports Tags:
     *                 - x1, y1, x2, y2, rX1, rY1, rX2, rY2, borderType, borderWidth, (background ->
     *                 bgImageRef)
     */
    public View(View parrent, Settings settings) {
        System.out.println("View: " + settings.name);
        this.parrent = parrent;
        this.settings = settings;
        this.settingsName = settings.name;
        String tempString;
        // Determines whether relative or absolute values are used
        tempString = settings.get("rX1");
        this.x1 = tempString != null ? (tempString.startsWith("-") ? Integer.parseInt(tempString) + parrent.x2 : Integer.parseInt(tempString) + parrent.x1) : Integer.parseInt(getSetting("x1", defaultSettings));
        tempString = settings.get("rX2");
        this.x2 = tempString != null ? (tempString.startsWith("-") ? Integer.parseInt(tempString) + parrent.x2 : Integer.parseInt(tempString) + parrent.x1) : Integer.parseInt(getSetting("x2", defaultSettings));
        tempString = settings.get("rY1");
        this.y1 = tempString != null ? (tempString.startsWith("-") ? Integer.parseInt(tempString) + parrent.y2 : Integer.parseInt(tempString) + parrent.y1) : Integer.parseInt(getSetting("y1", defaultSettings));
        tempString = settings.get("rY2");
        this.y2 = tempString != null ? (tempString.startsWith("-") ? Integer.parseInt(tempString) + parrent.y2 : Integer.parseInt(tempString) + parrent.y1) : Integer.parseInt(getSetting("y2", defaultSettings));
        this.width = x2 - x1;
        this.height = y2 - y1;
        System.out.println("x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2);
        String sSpeed = settings.get("scrollSpeed");
        if(sSpeed != null ){
            this.canBeScrolled = true;
            this.scrollSpeed = Double.parseDouble(sSpeed);
        }
        this.border = new Border(BorderType.valueOf(getSetting("borderType", defaultSettings)),
                Integer.parseInt(getSetting("borderWidth", defaultSettings)));
        this.bgImageRef = getSetting("background", defaultSettings);
        this.baseImage = ImageLoader.getImage(getSetting("background", defaultSettings));
        this.image = ImageHandler.getBorderImage(border, width, height, baseImage);

        if(this.parrent != null && !this.parrent.viewsList.contains(this)){
            this.parrent.viewsList.add(this);
        }
    }

    /** Invokes draw() of subViews, then invokes this.drawThis() */
    public final void draw(Graphics g) {
        if (active) {
            drawThis(g);
            for (View v : viewsList) {
                if (v.active) {
                    v.draw(g);
                }
            }
        }
    }

    public void drawThis(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (active) {
            if (this.image != null) {
                g2d.drawImage(this.image, x1, y1, width, height, null);
            }
        }
    }

    // Size and position adjustments

    public void setX1(int x) {
        this.x1 = x;
        this.x2 = x + width;
    }

    public void setY1(int y) {
        this.y1 = y;
        this.y2 = y + height;
    }

    public enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, TOP, BOTTOM, LEFT, RIGHT
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public void stretch(Position p, int x, int y) {
        switch (p) {
            case TOP_LEFT:
                x1 = x;
                y1 = y;
                width = x2 - x1;
                height = y2 - y1;
                System.out.println("x2:" + x2 + " x1+width:" + (x1 + width));
                break;
            case TOP_RIGHT:
                x2 = x;
                y1 = y;
                width = x2 - x1;
                height = y2 - y1;
                break;
            case BOTTOM_LEFT:
                x1 = x;
                y2 = y;
                width = x2 - x1;
                height = y2 - y1;
                break;
            case BOTTOM_RIGHT:
                x2 = x;
                y2 = y;
                width = x2 - x1;
                height = y2 - y1;
                break;
            case CENTER:
                double middleX = (x1 + x2) / 2;
                double middleY = (y1 + y2) / 2;
                double halfXlength = Math.abs(x2 - x1) / 2.0;
                if (x < halfXlength)
                    x = (int) halfXlength;
                if (x > Game.WIDTH - halfXlength)
                    x = Game.WIDTH - (int) halfXlength;
                double halfYlength = Math.abs(y2 - y1) / 2.0;
                if (y < halfYlength)
                    y = (int) halfYlength;
                if (y > Game.HEIGHT - halfYlength)
                    y = Game.HEIGHT - (int) halfYlength;
                int xDistance = (int) (x - middleX);
                int yDistance = (int) (y - middleY);
                if (x1 < x2) {
                    x1 += xDistance;
                    x2 += xDistance;
                } else {
                    x1 -= xDistance;
                    x2 -= xDistance;
                }
                if (y1 < y2) {
                    y1 += yDistance;
                    y2 += yDistance;
                } else {
                    y1 -= yDistance;
                    y2 -= yDistance;
                }
                break;
            case TOP:
                y1 = y;
                height = y2 - y1;
                break;
            case BOTTOM:
                y2 = y;
                height = y2 - y1;
                break;
            case LEFT:
                x1 = x;
                width = x2 - x1;
                break;
            case RIGHT:
                x2 = x;
                width = x2 - x1;
                break;
        }

        this.image = ImageHandler.getBorderImage(border, width, height, baseImage);

        stretchThis(p, x, y);
    }

    public void stretchThis(Position position, int x, int y) {

    }

    // Getter

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public final void handleInputs() {
        ArrayList<View> tempViews = new ArrayList<View>(this.viewsList);
        for (int i = tempViews.size() - 1; i >= 0; i--)  {
            if (tempViews.get(i).active) {
                tempViews.get(i).handleInputs();
            }
        }
        Settings globals = Game.renderer.getSettings("global");
        handleThisInputs();
        if( isMouseOver()){
            /* System.out.println(settingsName); */
            if(KeyHandler.consumeKey(globals.get("leftclick"))) System.out.println(getClass());;
        }
    }

    /** Should be overriden by sub-classes */
    public void handleThisInputs() {

    }

    public void handleEditorInputs() {
        Settings editorSettings = Game.renderer.getSettings("editMode");
        System.out.println(editorSettings.get(new String[] {"up-left","up","up-right","left,","center","right","down-left","down","down-right"})[0]);
        int[] inputs = KeyHandler.expendKeys(editorSettings.get(new String[] {"up-left","up","up-right","left,","center","right","down-left","down","down-right"}),
                new String[] { "", "", "", "", "", "", "", "", "" },
                new String[] { "U", "U", "U", "U", "U", "U", "U", "U", "U" });
        int x = MouseMotionListener.mouseX;
        if(x == Game.WIDTH - 1)
            x = Game.WIDTH;
        int y = MouseMotionListener.mouseY;
        if(y == Game.HEIGHT - 1)
            y = Game.HEIGHT;
        
        for (int input : inputs) {
            switch (input) {
                case 0:
                    stretch(Position.TOP_LEFT, x, y);
                    break;
                case 1:
                    stretch(Position.TOP, x, y);
                    break;
                case 2:
                    stretch(Position.TOP_RIGHT, x, y);
                    break;
                case 3:
                    stretch(Position.LEFT, x, y);
                    break;
                case 4:
                    stretch(Position.CENTER, x, y);
                    break;
                case 5:
                    stretch(Position.RIGHT, x, y);
                    break;
                case 6:
                    stretch(Position.BOTTOM_LEFT, x, y);
                    break;
                case 7:
                    stretch(Position.BOTTOM, x, y);
                    break;
                case 8:
                    stretch(Position.BOTTOM_RIGHT, x, y);
                    break;
            }
        }
    }

    public boolean isMouseOver() {
        // x1 could be greater than x2 if the view is stretched to the left
        int xMiddle = (x1 + x2) / 2;
        int yMiddle = (y1 + y2) / 2;
        int xDistance = Math.abs(xMiddle - MouseMotionListener.mouseX);
        int yDistance = Math.abs(yMiddle - MouseMotionListener.mouseY);
        return xDistance < width / 2 && yDistance < height / 2;
    }

    public View selectOnCursor() {
        if (isMouseOver()) {
            View result = this;
            for (View v : views.values()) {
                View subResult = v.selectOnCursor();
                if (subResult != null)
                    result = subResult;
            }
            return result;
        } else
            return null;

    }

    public void getPreferencesFromSettings(String settingsName) {
        Settings settings = Game.renderer.settingsMap.get(settingsName);
        if (settings == null)
            return;
        // String to int
        this.x1 = Integer.parseInt(settings.get("x1"));
        this.x2 = Integer.parseInt(settings.get("x2"));
        this.width = x2 - x1;
        this.height = y2 - y1;

    }

    public void saveToSettings() {
        if (settingsName == null)
            return;
        settings = Game.renderer.settingsMap.get(settingsName);
    }

    public String getSetting(String s, Settings defaults) {
        String result = this.settings.get(s);
        if (result != null || defaults == null)
            return result;
        System.out.println("Warning: " + s + " not found in " + settingsName + " settings");
        return defaults.get(s);
    }

    public String getSetting(Settings defaults, String... s) {
        String result;
        for(String string : s) {
            result = this.settings.get(string);
            if (result != null || defaults == null)
                return result;
        }
        System.out.println("Warning: " + s[0] + " not found in " + settingsName + " settings");
        return defaults.get(s[0]);
    }

    /** Should be used to remove View and detach all views inside .views aswell. */
    public void remove() {
        ArrayList<View> tempViews = new ArrayList<View>();
        tempViews.addAll(views.values());
        for (View v : tempViews) {
            v.remove();
        }
        views.clear();
        for(String key : parrent.views.keySet()){
            if(parrent.views.get(key) == this){
                parrent.views.remove(key);
                break;
            }
        }
        parrent.viewsList.remove(this);
    }

}