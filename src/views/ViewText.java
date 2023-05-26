package views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import basis.FontHandler;
import basis.FontHandler.FontType;
import display.View;
import game.Settings;

/** Should incorporate DisplayedText */
public class ViewText extends View {

    private String text;
    /** Currently unused */
    private Color textColor;
    private FontType font;
    /** Currently unused */
    private Alignment textAlignment;
    public Image textImage;
    private int textSize;
    /** Currently unused. */
    public TextPosition position;
    public int textDistanceToBorder;

    public static Settings defaultSettings = new Settings("defaults", new String[] { "text", "DEFAULTTEXT",
            "textColor", String.valueOf(Color.WHITE.getRGB()), "textFont", "DEFAULT", "textAlignment", "CENTER",
            "textSize", "64", "textDistanceToBorder", "1", "position", "CENTER" });

    /**
     * @param settings supports additional Tags:
     *                 - text, textColor, font, textAlignment, textSize,
     *                 textDistanceToBorder, position
     * @see View#View(Settings)
     */
    public ViewText(View parrent,Settings settings) {
        super(parrent,settings);
        // Color.WHITE, Alignment.CENTER, FontType.DEFAULT, 64,1, TextPosition.CENTER)
        this.text = getSetting("text", defaultSettings);
        this.textColor = new Color(Integer.parseInt(getSetting("textColor", defaultSettings)));
        this.font = FontType.valueOf(getSetting("textFont", defaultSettings));
        this.textAlignment = Alignment.valueOf(getSetting("textAlignment", defaultSettings));
        this.textSize = Integer.parseInt(getSetting("textSize", defaultSettings));
        this.textDistanceToBorder = Integer.parseInt(getSetting("textDistanceToBorder", defaultSettings));
        this.position = TextPosition.valueOf(getSetting("position", defaultSettings));
        this.textImage = createTextImage();

    }

    @Override
    public void stretchThis(Position p, int x, int y) {
        textImage = createTextImage();
    }

    public Image createTextImage() {
        return FontHandler.createImage(text, textColor, font, textAlignment,
                getWidth() - (border.width * 2 + textDistanceToBorder * 2),
                getHeight() - (border.width * 2 + textDistanceToBorder * 2), textSize);
    }

    public enum TextPosition {
        CENTER, LEFT, RIGHT
    };

    @Override
    public void drawThis(Graphics g) {
        super.drawThis(g);
        g.drawImage(textImage, getX1() + border.width + textDistanceToBorder,
                getY1() + border.width + textDistanceToBorder, null);
    }

    // Setters and getters

    public void setText(String text) {
        this.text = text;
        textImage = createTextImage();
    }

    public void setColor(Color textColor) {
        this.textColor = textColor;
        textImage = createTextImage();
    }

    public void setFont(FontType font) {
        this.font = font;
        textImage = createTextImage();
    }

    public void setAlignment(Alignment textAlignment) {
        this.textAlignment = textAlignment;
        textImage = createTextImage();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textImage = createTextImage();
    }

    public void setTextDistanceToBorder(int textDistanceToBorder) {
        this.textDistanceToBorder = textDistanceToBorder;
    }

    public int getTextDistanceToBorder() {
        return textDistanceToBorder;
    }

    public int getFontHeight() {
        return textSize;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return textColor;
    }

    public FontType getFont() {
        return font;
    }

    public Alignment getAlignment() {
        return textAlignment;
    }
}
