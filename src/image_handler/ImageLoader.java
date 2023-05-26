package image_handler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import basis.GameFrame;

/** Loads Images from drive and stores them into a map */
public class ImageLoader {

	/** Map of all images */
	private static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();

	private static String[] fileNames = new String[] {
			"buttonBackground", "default_border", "font", "fontOutline", "mainMenuBackground", "textInput","bright_border"
	};

	/**
	 * Loads an image via images/"name" and stores it into images map
	 * 
	 * @param fileName name of the image on drive, and future key inside images map
	 */
	private static void loadImage(String fileName) {
		try {
			BufferedImage img = ImageIO.read(ImageLoader.class.getResourceAsStream("/" + fileName));
			images.put(fileName.substring(0, fileName.length() - 4), img);
		} catch (Exception e) {
			System.err.println("Cant find Resource: " + e);
		}
	}

	public static BufferedImage getImage(String name) {
		BufferedImage result = images.get(name);
		if (result == null) {
			BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.createGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(1, 1, 2, 2);
			g.fillRect(0, 0, 1, 1);
			g.setColor(new Color(255, 128, 255, 255));
			g.fillRect(1, 0, 2, 1);
			g.fillRect(0, 1, 1, 2);
			g.dispose();
			result = img;

		}
		return result;
	}

	public static void init() {

		if(!GameFrame.dev){
			for (String s : fileNames) {
				System.out.println(s);
				loadImage(s + ".png");
			}
			return;
		}
		/** Load all images in res/images into images-Array */
		File f = new File(ImageLoader.class.getResource("/").getPath());
		for (String s : f.list()) {
			if (!s.endsWith(".png"))
				continue;
			System.out.println(s);
			loadImage(s);
		}

		System.out.println("Finished loading images.");
	}
}
