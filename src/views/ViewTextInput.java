package views;

import java.util.ArrayList;

import basis.KeyHandler;
import display.View;
import game.Game;
import game.Settings;

public class ViewTextInput extends ViewText {
    public boolean selected = false;
    public submitFunction submitFunction;

    public ViewTextInput(View parrent,Settings settings) {
        super(parrent, settings);
    }

    public ViewTextInput(View parrent, Settings settings, submitFunction submitFunction) {
        super(parrent,settings);
        this.submitFunction = submitFunction;
        System.out.println("Created text input: " + getX1() + " " + getY1() + " " + getX2() + " " + getY2());
    }

    /** Is called when "Enter"-Key is pressed while selected */
    public void submit(){
        if(submitFunction != null){
            submitFunction.func(getText());
        }
    }

    @Override
    public void handleThisInputs() {
        Settings globalSettings = Game.renderer.getSettings("global");

        if (isMouseOver()) {
            if (KeyHandler.consumeKey(globalSettings.get("leftclick"))) {
                selected = true;
            }
        }

        if (selected) {
            String text = getText();
            if (KeyHandler.consumeKey(globalSettings.get("backspace"))) {
                if (text.length() > 0) {
                    setText(text.substring(0, text.length() - 1));
                }
            } else if (KeyHandler.consumeKey(globalSettings.get("enter"))) {
                selected = false;
                submit();
            } else {
                ArrayList<String> keys = new ArrayList<>(KeyHandler.CURRENTLY_PRESSED);
                for(String s : keys){
                    if(s.length() == 1){
                        text += s;
                        setText(text);
                        KeyHandler.consumeKey(s);
                    }
                }
            }
        }
    }

    public interface submitFunction{
        void func(String s);
    }

}
