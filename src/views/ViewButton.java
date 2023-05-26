package views;

import basis.KeyHandler;
import display.View;
import game.Settings;

public class ViewButton extends ViewText {
    public functionInterface fInterface;

    public ViewButton(View parrent,Settings settings, functionInterface fInterface) {
        super(parrent,settings);
        this.fInterface = fInterface;
    }

    public interface functionInterface {
        void func();
    }

    @Override
    public void handleThisInputs() {
        if (isMouseOver()) {
            if (KeyHandler.consumeKey("LEFTCLICK")) {
                fInterface.func();
            }
        }
    }
}
