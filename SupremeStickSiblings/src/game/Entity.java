package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {
	
	/**This abstract class exists for the enemy and user class, as they share a lot of the same methods and act similarly. What is in this class
	 * that aren't abstract are methods that apply for both the user and enemy, such as drawing the hitbox that is in the main class, or displaying their
	 * energy/health bars in the main class. these wouldn't operate well in their own classes which is why it's in this entity class. 
	 * 
	 */
	
	public abstract void falling();
	public abstract void jump();
	public abstract void limit();
	public abstract void crouch();
	public abstract void display(Graphics g);
	public abstract void head(Graphics g, int x, int y, Color color);
	public abstract void body(Graphics g, int x, int y, Color color);
	public void drawHitbox(int x,int y, Rectangle rect, Graphics g) {
		g.setColor(Color.orange);
		g.drawRect(x, y, (int) rect.getWidth(), (int) rect.getHeight());
	}
	public void displayBar(Graphics g, int type, int x, int y, Color color) {
		g.setColor(Color.black);
		g.drawString(" " + type, x+55, y-25);
		g.drawRect(x-50, y-50, 105, 35);
		g.setColor(color);
		g.fillRect(x-47, y-47, type, 30);
		
	}
}
