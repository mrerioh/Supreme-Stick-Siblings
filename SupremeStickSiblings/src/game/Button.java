package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class Button extends FontMetrics{
	private int x,y;
	private String text;

	//when calling outside this class, it will use the x, y, and text declared in the other class in this class and returns it back
	public Button(int x, int y,String text) {
		this.x=x;
		this.y=y;
		this.text=text;
	}
	//display method that accepts Graphics g to display the user's character
	public void display(Graphics g){
		g.setColor(new Color(244, 185, 66));
		g.fillRect(x,y,100,50);
		g.setColor(Color.white);
		g.drawString(text, x + 50 - getMetrics(text,g)/2, y+30);
	}
	//method clicked returns the text of the button that is pressed, and tracks the user's mouse position and check if its in the parameters when clicked
	public String clicked(MouseEvent arg0){
		int mouseX = arg0.getX();
		int mouseY = arg0.getY();
		if(mouseX>=this.x && mouseX<=this.x+100 && mouseY>=this.y && mouseY<=this.y+50){
			return text;
		} 
		return "";
	}
	
	


}
