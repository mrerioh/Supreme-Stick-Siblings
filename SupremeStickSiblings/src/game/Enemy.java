package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Enemy extends Entity{
	
	/**THIS CLASS OPERATES ALMOST EXACTLY THE SAME AS THE USER CLASS, WITH DISTINCTIONS IN A FEW AREAS. AS A RESULT, THE COMMENTING IS IN THE
	 * USER CLASS INSTEAD OF THIS CLASS.
	 * 
	 */

	public int aiX = 850;
	public int aiY = 450;
	public int dx =50;
	private int gravity = 10;
	private int maxY=450;
	private int dy = 50;
	private boolean falling = true;
	private boolean jumping = true;
	public boolean crouching = false;
	public int health = 100;
	public boolean hitting=false;
	public boolean charDir=false;
	public Rectangle aihead = new Rectangle(aiX,aiY,30,30);
	public Rectangle aibody = new Rectangle(aiX,aiY+30,30,55);
	public Rectangle aiRight = new Rectangle(aiX+15, aiY+40, 30 , 10);
	public Rectangle aiLeft = new Rectangle(aiX-15, aiY+40, 30 , 10);
	Color aiColor = new Color(255, 232, 58);

	public Enemy(int aiX, int aiY) {
		this.aiX=aiX;
		this.aiY=aiY;		
	}
	@Override
	public void falling() {
		if(falling) {
			aiY+=gravity;
			if(aiY>maxY) {
				aiY=maxY;
			}
			if(aiY<425) {
				aiY=425;
			}
		}
		jumping=true;

	}

	public void crouch() {
		if(crouching) {
			aiY+=30;
		}
	}

	@Override
	public void jump() {
		if(jumping) {
			aiY -= dy;
			jumping=false;
			falling=true;
		}
		aiY+=gravity;
		if(aiY>maxY) {
			aiY=maxY;
		}
	}

	@Override
	public void limit() {
		if(aiX<30) {
			aiX=30;
		} else if(aiX>960) {
			aiX=960;
		}
	}

	public void display(Graphics g) {
		// TODO Auto-generated method stub
		if(!crouching) {
			head(g, getX(), getY(), aiColor);
		} if(crouching) {
			head(g,getX(),getY()+40, aiColor);
		}
		body(g, getX(), getY(), aiColor);
	}

	@Override
	public void head(Graphics g, int x, int y, Color color) {
		g.setColor(color);
		g.fillOval(x, y, 30, 30);

	}

	@Override
	public void body(Graphics g, int x, int y, Color color){
		g.setColor(color);
		if(!crouching) {
			g.drawLine(x+15, y+10 , x+15 , y+80); 
		} if(crouching) {
			g.drawLine(x+15, y+45 , x+15 , y+80);
		}
		if(!hitting && charDir) {
			//top arm
			g.drawLine(x+15, y+40, x+25, y+50);
			g.drawLine(x+25, y+50, x+40, y+40);
			//bottom arm
			g.drawLine(x+10, y+50, x+20, y+60);
			g.drawLine(x+20, y+60, x+35, y+50);
		} else if(hitting && charDir) {
			//top arm
			g.drawLine(x+15, y+40, x+25, y+50);
			g.drawLine(x+25, y+50, x+40, y+40);
			//hitting arm
			g.drawLine(x+15, y+50, x+45, y+50);			
		} else if(!hitting && !charDir) {
			//top arm
			g.drawLine(x+15, y+40, x, y+50);
			g.drawLine(x, y+50, x-15, y+40);
			//bottom arm
			g.drawLine(x+20, y+50, x, y+60);
			g.drawLine(x, y+60, x-15, y+50);
		} else if(hitting && !charDir) {
			//top arm
			g.drawLine(x+15, y+40, x, y+50);
			g.drawLine(x, y+50, x-15, y+40);
			//hitting arm
			g.drawLine(x+15, y+50, x-15, y+50);	
			
		}
		
		g.drawLine(x+15, y+80 , x , y+85); 
		g.drawLine(x+15, y+80 , x+30 , y+85); 
	}

	public int getX() {
		return aiX;
	}

	public int getY() {
		return aiY;
	}

}
