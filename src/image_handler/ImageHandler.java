package image_handler;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import display.Border;

public class ImageHandler {

	/**
	 * Returns an image for a viewm, using its border
	 * 
	 * @param border is the border surrounding the image of the view. If null, no border will be drawn.
	 * @param width  is the new width of the image
	 * @param height is the new height of the image
	 * @param innerImage is the image to be drawn inside the border. If null, the borders interior will be drawn instead.
	 */
	public static Image getBorderImage(Border border, int width, int height, Image innerImage) {
		width = width == 0 ? 1 : width;
		height = height == 0 ? 1 : height;
		if(border == null || border.type == Border.BorderType.NONE || border.width == 0){
			return ImageScaler.fitImage(innerImage, width, height);
		}
		int bWidth = border.width;
		int sWidth = border.sourceWidth;
			int bImgWidth = border.image.getWidth(null);
			int bImgHeight = border.image.getHeight(null);
			BufferedImage destImg = new BufferedImage(Math.abs(width), Math.abs(height), BufferedImage.TYPE_INT_ARGB);
			Graphics2D tempG = destImg.createGraphics();
			if (innerImage != null) {
				tempG.drawImage(innerImage, bWidth , bWidth , width - bWidth ,
						height - bWidth , 0, 0, innerImage.getWidth(null), innerImage.getHeight(null), null);
			} else {
				// Inner "big" Part to scale
				tempG.drawImage(border.image, bWidth , bWidth , width - bWidth ,
						height - bWidth , sWidth, sWidth, bImgWidth - sWidth, bImgHeight - sWidth, null);
			}
			// Upper-Left Corner
			tempG.drawImage(border.image, 0, 0, bWidth , 
			bWidth, 0, 0, sWidth, sWidth, null);
			// Upper-Right Corner
			tempG.drawImage(border.image, width - bWidth , 0, width, 
			bWidth , bImgWidth - sWidth, 0, bImgWidth, sWidth, null);
			// Lower-Left Corner
			tempG.drawImage(border.image, 0, height - bWidth , bWidth , 
			height, 0, bImgHeight - sWidth, sWidth, bImgHeight, null);
			// Lower-Right Corner
			tempG.drawImage(border.image, width - bWidth , height - bWidth , width, height, 
			bImgWidth - sWidth, bImgHeight - sWidth, bImgWidth, bImgHeight, null);
			// Upper-Border
			tempG.drawImage(border.image, bWidth , 0, width - bWidth , bWidth , 
			sWidth, 0, bImgWidth - sWidth, sWidth, null);
			// Lower-Border
			tempG.drawImage(border.image, bWidth , height - bWidth , width - bWidth ,height, 
			sWidth, bImgHeight - sWidth, bImgWidth - sWidth, bImgHeight, null);
			// Left-Border
			tempG.drawImage(border.image, 0, bWidth , bWidth , height - bWidth , 
			0, sWidth, sWidth, bImgHeight - sWidth, null);
			// Right-Border
			tempG.drawImage(border.image, width - bWidth , bWidth , width, height - bWidth ,
			bImgWidth - sWidth, sWidth, bImgWidth, bImgHeight - bWidth, null);
			//
			tempG.dispose();
		return destImg;
	}
}
