package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {
	
	private BufferedImage image;
	
	public Images(String fileName) {
		try {
			 image = ImageIO.read(new File("icons/" + fileName + ".png"));
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void drawImage(Graphics g, int x, int y, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
	}
	

}
