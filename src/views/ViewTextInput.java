package views;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import basis.KeyHandler;
import display.View;
import game.Game;
import game.Settings;

public class ViewTextInput extends ViewText {
    public boolean selected = false;
    public submitFunction submitFunction;
    public double animationState = 0;
    public int backspaceDuration = 0;

    public ViewTextInput(View parrent, Settings settings) {
        super(parrent, settings);
    }

    public ViewTextInput(View parrent, Settings settings, submitFunction submitFunction) {
        super(parrent, settings);
        this.submitFunction = submitFunction;
        System.out.println("Created text input: " + getX1() + " " + getY1() + " " + getX2() + " " + getY2());
    }

    /** Is called when "Enter"-Key is pressed while selected */
    public void submit() {
        if (submitFunction != null) {
            submitFunction.func(getText());
        }
    }

    @Override
    public void drawThis(Graphics g) {
        super.drawThis(g);
        g.drawImage(textImage, getX1() + border.width + textDistanceToBorder,
                getY1() + border.width + textDistanceToBorder, null);
        // Todo: should be in TextImageGeneration
        if (selected && animationState <= 0.5) {
            g.setColor(Color.BLACK);
            g.fillRect(getX1() + border.width + textDistanceToBorder + textImage.getWidth(null) - 1,
                    getY1() + border.width + textDistanceToBorder,
                    4, textImage.getHeight(null));
        }
    }

    @Override
    public void handleThisInputs() {
        Settings globalSettings = Game.renderer.getSettings("global");
        animationState += 0.5 / Game.tick;
        if (animationState > 1)
            animationState -= 1;
        if (isMouseOver()) {
            if (KeyHandler.consumeKey(globalSettings.get("leftclick"))) {
                selected = true;
                animationState = 0;
            }
        }

        if (selected) {
            String text = getText();
            if (KeyHandler.useKeyForTick(globalSettings.get("backspace"))) {
                if (text.length() > 0) {
                    if (backspaceDuration > Game.tick) {
                        if (backspaceDuration % (Game.tick / 15) == 0)
                            setText(text.substring(0, text.length() - 1));
                    } else if (backspaceDuration % (Game.tick / 5) == 0)
                        setText(text.substring(0, text.length() - 1));
                }
                backspaceDuration += 1;
            } else {
                backspaceDuration = 0;
                if (KeyHandler.consumeKey(globalSettings.get("enter"))) {
                    selected = false;
                    submit();
                } else {
                    ArrayList<String> keys = new ArrayList<>(KeyHandler.CURRENTLY_PRESSED);
                    for (String s : keys) {
                        if (s.length() == 1) {
                            text += s;
                            setText(text);
                            KeyHandler.consumeKey(s);
                        }
                    }
                }
            }
        }
    }

    public interface submitFunction {
        void func(String s);
    }

}
