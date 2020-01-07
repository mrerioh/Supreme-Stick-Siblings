package game;

import java.awt.Graphics;

public abstract class FontMetrics {
	
	//gets the font size in pixels
	public int getMetrics(String text, Graphics g) {
		return g.getFontMetrics().stringWidth(text);
	}

}
