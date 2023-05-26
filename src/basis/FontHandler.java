package basis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import display.View.Alignment;
import image_handler.ImageLoader;

public class FontHandler {

    public enum FontType {
        DEFAULT, BORDERED;
    }

    public static Map<FontType,BufferedImage> fontImages = new HashMap<>();

    /** Should be adjusted to required fonts. Each new font has to be added to this method. */
    public static void init(){
        fontImages.put(FontType.DEFAULT,ImageLoader.getImage("font"));
        fontImages.put(FontType.BORDERED,ImageLoader.getImage("fontOutline"));
        charPositionsMap.put(FontType.DEFAULT, new int[128]);
        charPositionsMap.put(FontType.BORDERED, new int[128]);
        int fontSourceHeight = fontImages.get(FontType.DEFAULT).getHeight(null);
        int sourceCharWidth = fontSourceHeight;

        int pointer = 0;
        for (FontHandler.FontType key : charPositionsMap.keySet()) {
            pointer = 0;
            int[] positions = charPositionsMap.get(key);
            int borderWidth = 0 + (key == FontType.BORDERED ? outlineSize : 0);
            for(int i = 0; i < characters.length; i++){
             widths[characters[i]] = charWidths[i];
                positions[characters[i]] = pointer + (sourceCharWidth / 2) - (charWidths[i] / 2) - borderWidth;
                pointer += fontSourceHeight;
            }
        }
    }

    public static double charSpaceFactor = 1.0/8;
    public static int outlineSize = 2;
    private static char[] characters = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9','.'};
    private static int[] charWidths  = new int[] { 40, 40, 40, 40, 40, 32, 40, 40,  8, 32, 32,  8, 40, 40, 40, 40, 40, 40, 50, 32, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 8,  40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 16, 40, 40, 40, 40, 40, 40, 40, 40,  8};
    private static int[] widths = new int[128];
    /** Starting x of character in font-file -> x + charWidth == End x of char */
    public static Map<FontType,int[]> charPositionsMap = new HashMap<>();

    public static BufferedImage createImage(String text, Color color,FontType font,Alignment alignment,int maxWidth,int maxHeight, int rowHeight){
        System.out.println(font + " " + alignment + " " + maxWidth + " " + maxHeight + " " + rowHeight);
        maxWidth = maxWidth == 0 ? 1 : maxWidth;
        maxHeight = maxHeight == 0 ? 1 : maxHeight;
        BufferedImage image = new BufferedImage(Math.abs(maxWidth), Math.abs(maxHeight), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        BufferedImage fontImage = fontImages.get(font);
        int sourceFontHeight = fontImage.getHeight();
        int charSpace = (int) (charSpaceFactor * sourceFontHeight);
        int[] charPositions = charPositionsMap.get(font);
        int borderWidth = (font == FontType.BORDERED ? outlineSize : 0);
        double scale = (double)rowHeight / (double)sourceFontHeight;

        int x = 0;
        int y = 0;

        while(y + rowHeight <= maxHeight && text.length() > 0){
            char c = text.charAt(0);
            text = text.substring(1);
            if(c == '\n'){
                x = 0;
                y += rowHeight;
                continue;
            }
            if(c == ' '){
                x += rowHeight / 2;
                continue;
            }
            int width = (int)((widths[c] + borderWidth*2) * scale);
            int sourceWidth = widths[c] + borderWidth*2;
            if(x + width > maxWidth){
                x = 0;
                y += rowHeight;
            }
            if(y + rowHeight > maxHeight){
                break;
            }
            g.drawImage(fontImage, x, y, x + width, y + rowHeight, 
                    charPositions[c], 0, charPositions[c] + sourceWidth, sourceFontHeight, null);
            x += width + charSpace;
        }
        if(x != 0 && alignment == Alignment.CENTER){
            int width = x;
            x = (maxWidth- width + charSpace) /2;
            int height = y + rowHeight;
            y = (maxHeight - height) / 2;
            BufferedImage tempImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics tempG = tempImg.getGraphics();
            tempG.drawImage(image, x, y, x + width, y + height, 0, 0, width, height, null);
            image = tempImg;
        }

        g.dispose();
        return image;
    }

}
