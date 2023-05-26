package image_handler;

import java.awt.image.BufferedImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

public class ImageScaler {

	public static double[] zoomStages = { 0.25, 0.5, 1, 2, 4 };

	/** Scales given images to the desired width and height and pre scales them to all zoomStages
	 * @param images Images to be scaled
	 * @param width Width of the scaled images
	 * @param height Height of the scaled images
	 * @return Scaled images: scaledImages[images][zoomStages]
	 */
	public static BufferedImage[][] scaleImages(Image[] images, int width, int height) {
		width = width == 0 ? 1 : width;
		height = height == 0 ? 1 : height;
		BufferedImage[][] scaledImages = new BufferedImage[images.length][zoomStages.length];
		for (int h = 0; h < images.length; h++) {

			for (int i = 0; i < zoomStages.length; i++) {
				AffineTransform aT = new AffineTransform();
				aT.scale(zoomStages[i] * width / images[0].getWidth(null),
						zoomStages[i] * height / images[0].getHeight(null));
				BufferedImage tempImg = new BufferedImage((int) (Math.abs(width) * zoomStages[i]),
						(int) (Math.abs(height) * zoomStages[i]), BufferedImage.TYPE_INT_ARGB);
				Graphics2D tempG = tempImg.createGraphics();
				tempG.drawImage(images[h], aT, null);
				tempG.dispose();
				scaledImages[h][i] = tempImg;
			}

		}
		return scaledImages;
	}

	/** Scales given image to the desired width and height and pre scales it to all zoomStages
	 * @param image Image to be scaled
	 * @param width Width of the scaled image
	 * @param height Height of the scaled image
	 * @return Scaled image: scaledImage[zoomStages]
	 * @note Use fitImage() if you only want the image to be scaled to the desired width and height, without pre scaling it to all zoomStages
	 */
	public static Image[] scaleImage(Image image, int width, int height) {
		width = width == 0 ? 1 : width;
		height = height == 0 ? 1 : height;
		Image[] scaledImage = new Image[zoomStages.length];
		for (int i = 0; i < zoomStages.length; i++) {
			AffineTransform aT = new AffineTransform();
			aT.scale(zoomStages[i] * width / image.getWidth(null),
					zoomStages[i] * height / image.getHeight(null));
			BufferedImage tempImg = new BufferedImage((int) (Math.abs(width) * zoomStages[i]), (int) (Math.abs(height) * zoomStages[i]),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D tempG = tempImg.createGraphics();
			tempG.drawImage(image, aT, null);
			tempG.dispose();
			scaledImage[i] = tempImg;

		}
		return scaledImage;
	}

	/** Scales an Image to a size */
	public static Image fitImage(Image image, int width, int height) {
		width = width == 0 ? 1 : width;
		height = height == 0 ? 1 : height;
		AffineTransform aT = new AffineTransform();
		aT.scale((double)width / image.getWidth(null),
				(double)height / image.getHeight(null));
		BufferedImage tempImg = new BufferedImage((int) Math.abs(width), (int) Math.abs(height),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D tempG = tempImg.createGraphics();
		tempG.drawImage(image, aT, null);
		tempG.dispose();
		return tempImg;
	}
}
