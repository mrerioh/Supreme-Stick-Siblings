package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;


public class User extends Entity{

	public int userX=50;
	public int userY=450;
	private int gravity = 10;
	private int maxY=450;
	public int dy = 50;
	private boolean falling = true;
	private boolean jumping = true;
	public boolean crouching= false;
	public int energy = 100;
	public double aggressiveness = 20;
	public int health = 100;
	public boolean hitting = false;
	public boolean charDir = true;
	public boolean jumped = false;
	//hitboxes
	public Rectangle userhead = new Rectangle(userX,userY,30,30);
	public Rectangle userbody = new Rectangle(userX,userY+30,30,55);
	public Rectangle userRight = new Rectangle(userX+15, userY+40, 30 , 10);
	public Rectangle userLeft = new Rectangle(userX-15, userY+40, 30 , 10);
	Color userColor = new Color(212, 66, 244);


	public User(int userX,int userY, int energy, int aggressiveness) {
		this.userX=userX;
		this.userY=userY;
		this.energy=energy;
		this.aggressiveness=aggressiveness;
	}
	
	/*This method acts like the gravity system for the user. If they're not jumping, they're falling at the rate of gravity.
	 * 
	 * 
	 */
	public void falling() {
		if(falling) {
			userY+=gravity;
			if(userY>maxY) {
				userY=maxY;
			}
			if(userY<425) {
				userY=425;
			}
		}
		jumping=true;
		energy++;
	}

	//this method changes the userY when they crouch
	public void crouch() {
		if(crouching) {
			userY+=30;
		}
	}
	
	/*This jump method allows the user to jump at a rate of dy (change in distance y) plus the user's energy. They fall after
	 * 
	 */
	public void jump() {
		if(jumping) {
			userY -= dy + energy;
			jumping=false;
			falling=true;
		}
		userY+=gravity;
		if(userY>maxY) {
			userY=maxY;
		}
		jumped=true;
	}

	//this stops the user from leaving the screen
	public void limit() {
		if(userX<30) {
			userX=30;
		} else if(userX>960) {
			userX=960;
		}
	}
	
	//This displays the user's body and legs
	public void display(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));
		if(!crouching) {
			head(g, getX(), getY(), userColor);
		} if(crouching) {
			head(g,getX(),getY()+40, userColor);
		}
		body(g2, getX(), getY(), userColor);

	}

	
	public void head(Graphics g, int x, int y, Color color) {
		g.setColor(color);
		g.fillOval(x, y, 30, 30);

	}

	/**
	 * the body design changes often, depending on the direction the user is facing and what actions they are performing, and this method changes it.
	 * If they're crouching, their body height decreases.
	 * If they're not hitting and looking right, their arms face right in a boxing stance
	 * If they are hitting, one arm points to the enemy
	 * this works for the left direction too
	 */
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
		return userX;
	}
	public int getY() {
		return userY;
	}

	//This boolean method checks if int x is in range of the comparedX, from the direction specified
	public boolean inRange(int x, int comparedX, int range, String directionFrom) {
		if(directionFrom.equals("left")) {
			return x>comparedX-range && x<=comparedX;
		} else if(directionFrom.equals("right")) {
			return x<comparedX+range && x>=comparedX;
		} else {
			return false;
		}
	}
	

}
