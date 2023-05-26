package display;

import java.awt.Image;

import image_handler.ImageLoader;

/** Holds information of a border.
 */
public class Border {

    /** Thickness of the border */
    public int width = 0;
    /** Selected Border type. "NONE" results in no border */
    public BorderType type  = BorderType.NONE;
    /** Final corresponding image to "type" of the border. Loaded from ImageLoader.images */
    public final Image image;
    /** Final width of border inside source-image */
    public final int sourceWidth;

    public enum BorderType {
        NONE, DEFAULT, BRIGHT
    };

    public Border(BorderType type, int width) {
        this.type = type;
        this.width = width;
        switch(this.type) {
            case NONE:
                this.image = null;
                this.sourceWidth = 0;
                break;
            case DEFAULT:
                this.image = ImageLoader.getImage("default_border");
                this.sourceWidth = 2;
                break;
            case BRIGHT:
                this.image = ImageLoader.getImage("bright_border");
                this.sourceWidth = 2;
                break;
            default:
                this.image = null;
                this.sourceWidth = 0;
                break;
        };
    }

    
}
