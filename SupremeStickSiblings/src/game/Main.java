package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

/***
 * 
 * Super Stick Siblings
 * 
 * @author gfdshh
 * 
 * Description:
 *
 */

public class Main extends Canvas implements Runnable, MouseInputListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean running;
	private boolean gameOver = false;;
	
	//object declarations
	User user = new User(50,450,100,20);
	Enemy enemy = new Enemy(850,450);
	Images energyIcon, healthIcon, username, wallpaper, enemyname, menuScreen;
	Button play = new Button(430,300,"Play");
	Random rand = new Random();
	
	private String scene = "Menu"; //controls the scenes showing
	boolean isTired = false;
	boolean userWon= false;
	boolean aiWon= false;
	long refresh = 0;
	int randAI = 0;
	int mouseX=0;
	int mouseY=0;
	boolean hit = false;
	boolean debug = false;

	public Main() {
		//Creates the KeyInput object from the KeyInput class, as well as adds a key listener and mouse listener
		KeyInput key = new KeyInput();
		addKeyListener(key);
		addMouseListener(this); 
		addMouseMotionListener(this);
		//specifies the location and file name of the image
		energyIcon = new Images("energy");
		healthIcon = new Images("health");
		username = new Images("user");
		wallpaper = new Images("wallpaper");
		enemyname = new Images("enemy");
		menuScreen = new Images("menu");
	}

	//the tick method is updated in the run method, and refreshes and updates along with it
	private void tick() {
		////USER MECHANICS
		
		//If the user presses space, they jump, but if they press space and a directional key, they will jump in that direction, otherwise fall
		if(KeyInput.isPressed(KeyEvent.VK_SPACE)) {
			user.jump();
			user.energy-=3;
			if(KeyInput.isPressed(KeyEvent.VK_D) && user.energy>0){
				user.charDir=true;
				user.jump();
				user.energy-=4;
				user.userX+=5;
			}
			if(KeyInput.isPressed(KeyEvent.VK_A) && user.energy>0){
				user.charDir=false;
				user.jump();
				user.energy-=4;
				user.userX-=5;
			}

		} else {
			user.falling();	
		}
		
		
		//This is the crouching mechanic, which will only work when the user is not jumping but will also make the enemy crouch
		if(KeyInput.isPressed(KeyEvent.VK_CONTROL) && !KeyInput.isPressed(KeyEvent.VK_SPACE)) {
			enemy.crouching=true;
			user.crouching=true;
			enemy.crouch();
			user.crouch();
			user.falling();
		} else {
			enemy.crouching=false;
			user.crouching=false;
		}
		
		//this turns the user right and makes them move right. If they collide with the enemy, they move at a slower pace. If they press shift, they run and lose energy
		if(KeyInput.isPressed(KeyEvent.VK_D)){
			user.charDir=true;
			if(KeyInput.isPressed(KeyEvent.VK_SHIFT)) {
				user.userX+= 5+ (user.energy/20);
				user.energy-=2;
			} else if(!collision()) {
				user.userX+=5;
			}
			if(collision()) {
				user.userX+=2;
			}
		}

		//this turns the user left, and works similarly to the right function
		if(KeyInput.isPressed(KeyEvent.VK_A)) {
			user.charDir=false;
			if(KeyInput.isPressed(KeyEvent.VK_SHIFT)) {
				user.userX-= 5+ (user.energy/20);
				user.energy-=2;
			} else if(!collision()){
				user.userX-=5;
			}
			if(collision()) {
				user.userX-=2;
			}
		}

		
		//this makes the user hit towards the right as long as they hold it, every time refresh hits a multiple of 10. if they hit the enemy, the enemy loses health based on the enemys stance
		if(KeyInput.isPressed(KeyEvent.VK_RIGHT) && refresh%10==0) {
			user.charDir=true;
			user.hitting^=true;
			if(userhitBody()) {
				enemy.health-=5;
				user.hitting=false;
			} else if(userhitHead()) {
				if(!enemy.crouching) {
					enemy.health-=10;
				} else if(enemy.crouching) {
					enemy.health-=2;
				}
				user.hitting=false;
			}	
		} 

		//same as above, but for the left
		if(KeyInput.isPressed(KeyEvent.VK_LEFT) && refresh%10==0) {
			user.charDir=false;
			user.hitting^=true;
			if(userhitBody()) {
				enemy.health-=5;
				user.hitting=false;
			} else if(userhitHead()) {
				if(!enemy.crouching) {
					enemy.health-=10;
				} else if(enemy.crouching) {
					enemy.health-=2;
				}
				user.hitting=false;
			}
		} 

		//to open the debug menu, press f1
		if(KeyInput.isPressed(KeyEvent.VK_F1)) {
			debug^=true;
		}

		////USER MECHANICS

		///AI BEHAVIOUR & LIMITATIONS
		
		//if the user backs off, their aggressiveness wears off
		if(user.getX()<enemy.getX() || user.getX()>enemy.getX()) {
			user.aggressiveness-=0.01;
		}
		//if the user approaches the enemy (based on the range, in this case 200 or 100), the aggressiveness factor increases which changes how the AI will react
		if(user.inRange(user.userX, enemy.aiX, 200, "left") || user.inRange(user.userX, enemy.aiX, 200, "right")) {
			user.aggressiveness+=0.1;
		} else if(user.inRange(user.userX, enemy.aiX, 100, "left") || user.inRange(user.userX, enemy.aiX, 100, "right")) {
			user.aggressiveness+=0.2;
		} else {
			user.aggressiveness-=0.1;
		}
		
		//user energy limits
		if(user.energy>100) {
			user.energy=100;
		}
		if(user.energy<0) {
			user.energy=0;
		}
		
		//user aggressiveness limits
		if(user.aggressiveness<=1) {
			user.aggressiveness=1;
		}
		if(user.aggressiveness>=20) {
			user.aggressiveness=20;
		}
		
		//the counter for many of the AI and users decisions incrementing
		refresh++;

		//every time refresh%==0, the randAI integer rolls from 1 to user aggressiveness that decides what the AI will do next
		if(refresh%5==0) {
			randAI = rand.nextInt((int)user.aggressiveness); 
			
		}


		//if randAI>5, the enemy will approach the user but keep a distance of 100 pixels
		if(randAI>5) {
			if(user.userX + 100 < enemy.aiX) {
				enemy.charDir=false;
				enemy.aiX-=8;
			}
		} else if(user.userX + 100 > enemy.aiX) {
			enemy.charDir=true;
			enemy.aiX+=8;
		}
		
		
		//if they collide, the AI will run away
		if(collision()) {
			if(user.userX<enemy.aiX) {
				enemy.charDir=false;
				enemy.aiX-=2;
			} else if(user.userX>enemy.aiX) {
				enemy.charDir=true;
				enemy.aiX+=2;
			}		
		}
		
		//the conditions for the AI to jump (if user jumped, randAI<10, and refresh%20==0 then jump) or if (randAI%5==0)
		if((user.jumped && randAI<10 && refresh%20==0) || randAI%5==0) {
			enemy.jump();
			user.jumped=false;
		} else {
			enemy.falling();
		}
		
		//if randAI is even, and the refresh rate is divisible by 5, then the enemy will go for a hit and if it collides then user health goes down
		if(randAI%2==0 && refresh%5==0) {
			enemy.hitting^=true;
			if(enemyhitBody()) {
				user.health-=5;
				enemy.hitting=false;
			} else if(enemyhitHead()) {
				if(!user.crouching) {
					user.health-=10;
				} else if(user.crouching) {
					user.health-=2;
				}
				enemy.hitting=false;
			}	
		}

		//stops them from leaving the screen
		user.limit();
		enemy.limit();
		
		
		
		//using the rectangle 2d class, the rectangles (hereby referred to as hitboxes) that check for collision are updated based on the user's new pos.
		if(!user.crouching) {
			user.userbody.setBounds(user.getX(), user.getY()+30, 30, 55);
			user.userhead.setBounds(user.getX(), user.getY(),30, 30);
			enemy.aibody.setBounds(enemy.getX(), enemy.getY()+30, 30, 55);
			enemy.aihead.setBounds(enemy.getX(), enemy.getY(), 30, 30);
		} else if(user.crouching) {
			user.userbody.setBounds(-1000, user.getY()+30, 30, 55);
			enemy.aibody.setBounds(-1000, enemy.getY()+30, 30, 55);
			user.userhead.setBounds(user.getX(), user.getY()+40,30, 30);
			enemy.aihead.setBounds(enemy.getX(), enemy.getY()+40, 30, 30);
		}
		//their arms
		user.userRight.setBounds(user.getX()+15, user.getY()+40,30, 10);
		user.userLeft.setBounds(user.getX()-15, user.getY()+40, 30, 10);
		enemy.aiRight.setBounds(enemy.getX()+15, enemy.getY()+40,30, 10);
		enemy.aiLeft.setBounds(enemy.getX()-15, enemy.getY()+40, 30, 10);

		
		//game over conditions
		if(user.health<=0) {
			aiWon=true;
			gameOver=true;
			scene="Menu";
		} else if(enemy.health<=0) {
			userWon=true;
			gameOver=true;
			scene="Menu";
		}
		


	}


	/*This method was borrowed from youtuber Teivodov, who taught me how to implement and use the Buffer strategy that works similarly to a flipbook.
	 * The next frames are pre rendered and just display after the current one. This renders the graphics in the run and tick functions.
	 * 
	 */
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs==null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		//this imports the font that I used, Ultra Fresh in a try and catch, and returns an error otherwise.
		try {
			GraphicsEnvironment ge = 
					GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("font/Ultra Fresh - Demo.ttf")));
		} catch (IOException|FontFormatException e) {
			//Handle exception
			e.printStackTrace();
		}

		g.setFont(new Font("Ultra Fresh - Demo", Font.PLAIN, 20));

		////////////////

		if(scene.equals("Menu")) {
			menuScreen.drawImage(g, 0, 0, 1000, 600);
			play.display(g);
			g.setColor(Color.black);
			if(aiWon) {
				g.drawString("The enemy wins the fight", 400, 500);
			} else if(userWon) {
				g.drawString("You win the fight", 400, 500);
			}
		} else if(scene.equals("Play")) {

			g.setColor(new Color(194, 231, 239));
			g.fillRect(0, 0, 1000, 600);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 525, 1000, 75);
			g.setColor(Color.gray);
			g.drawRect(0, 550, 1000, 200);
			
			wallpaper.drawImage(g, 0, 25,1000,500);
			user.display(g);
			enemy.display(g);			
			healthIcon.drawImage(g, 15, 50,30,30);
			energyIcon.drawImage(g, 195, 52,30,30);
			healthIcon.drawImage(g, 815, 52,30,30);
			username.drawImage(g, 50, 5,120,40);
			enemyname.drawImage(g, 850, 5, 120, 40);

			user.displayBar(g,user.energy, 280, 100, Color.ORANGE);
			user.displayBar(g, user.health, 100, 100, Color.RED);
			enemy.displayBar(g, enemy.health, 900, 100, Color.RED);

			if(debug) {
				debug(g);
			}
		}
		////////////////

		
		g.dispose();
		bs.show();
	}
	
	//This method starts the game
	private void start() {
		if(running) return;
		running=true;
		new Thread(this,"Game").start();
	}
	
	//This method stops the game
	private void stop() {
		if(!running) return;
		running=false;
		System.exit(0);
	}

	/*This method was borrowed from Teivodov, and this method operates similarly to the actionPerformed method although it doesn't wait for an action performed.
	 * It uses frames that are calculated using seconds. If the frames are falling behind the program, the unprocessed tick goes up and catches up. If it can
	 * render the program, fps goes up and the render method is called and the game is displayed
	 */
	@Override
	public void run() {
		requestFocus();
		double target = 60.0;
		double nsPT = 1000000000.0/target;
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double unprocessed = 0.0;
		int fps=0;
		boolean canRender = false;

		while(running) {
			long now=System.nanoTime();
			unprocessed+= (now-lastTime) / nsPT;
			lastTime=now;

			if(unprocessed>=1.0) {
				if(!gameOver) {
					tick();
					KeyInput.update();
				} else if(gameOver) {
					user.health=100;
					user.userX=50;
					enemy.health=100;
					enemy.aiX=850;
				}
				unprocessed--;
				canRender=true;			
			}else canRender=false;

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(canRender) {
				render();
				fps++;
			}

			if(System.currentTimeMillis()-1000 >timer) {
				timer+=1000;
				System.out.println("FPS: " + fps);
				fps=0;
			}
		}
	}
	
	//creates the Console window using JFrame. It also sets the console size to 1000x600.
	public static void main(String[] args) throws IOException {
		Main game = new Main();
		JFrame frame = new JFrame("Supreme Stick Siblings");
		frame.setSize(new Dimension(1000,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//	Display c = new Display();
		frame.add(game);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.err.println("exiting");
				game.stop();
			}
		});
		frame.setVisible(true);
		game.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(scene.equals("Menu")) {
			if(play.clicked(e).equals("Play")) {
				aiWon=false;
				userWon=false;
				gameOver=false;
				scene = "Play";
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//hit();

	}
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	/*This debug method was created by me so I could check properties and conditions of different objects to check if they're operating properly.
	 * I did not want it to interrupt the game, so I made it toggleable using the F1 key and it displays data all over the screen
	 * 
	 */
	public void debug(Graphics g) {
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.setColor(Color.black);
		g.drawString("user aggressiveness: " + Math.round(user.aggressiveness*100.00)/100.00, 200, 100);
		g.drawString("refresh " + refresh, 400, 100);
		g.drawString("randAI " + randAI, 500, 100);
		g.drawString("colliding? " + collision(), 600, 100);
		g.drawString("hitbox x: " + user.userhead.getX() , 600, 150);
		g.drawString("sprint speed: " + (5 + (user.energy/20)), 200, 150);
		g.drawString("user within 200 from left? " +  (user.userX>enemy.aiX-200 && user.userX<=enemy.aiX), 100, 200);
		g.drawString("user within 200 from right? " +   (user.userX<enemy.aiX+200 && user.userX>=enemy.aiX), 100, 220);
		g.drawString("user within 100 from left? " +  (user.userX>enemy.aiX-100 && user.userX<=enemy.aiX), 100, 240);
		g.drawString("user within 100 from right? " +  (user.userX<enemy.aiX+100 && user.userX>=enemy.aiX), 100, 260);
		g.drawString("posX: " + mouseX , mouseX, mouseY);
		g.drawString("posY: " + mouseY , mouseX, mouseY+20);
		if(!user.crouching) {
			enemy.drawHitbox(enemy.getX(), enemy.getY()+30, enemy.aibody, g);
			enemy.drawHitbox(enemy.getX(), enemy.getY(), enemy.aihead, g);
			user.drawHitbox(user.getX(), user.getY(), user.userhead, g);
			user.drawHitbox(user.getX(), user.getY()+30, user.userbody, g);
		} else if(user.crouching) {
			user.drawHitbox(user.getX(), user.getY()+40, user.userhead, g);
			enemy.drawHitbox(enemy.getX(), enemy.getY()+40, enemy.aihead, g);

		}
		user.drawHitbox(user.getX()+15, user.getY()+40, user.userRight, g);
		user.drawHitbox(user.getX()-15, user.getY()+40, user.userLeft, g);
		enemy.drawHitbox(enemy.getX()+15, enemy.getY()+40, enemy.aiRight, g);
		enemy.drawHitbox(enemy.getX()-15, enemy.getY()+40, enemy.aiLeft, g);
	}

	/*The collision method returns a boolean to check if the user hitbox intersects the enemys hitbox, using the Rectangle2D class.
	 * It gets the bounds that were set in the tick method, stores it in new rectangles and then those are checked.
	 * 
	 */
	public boolean collision() {

		Rectangle userHeadbox = user.userhead.getBounds();
		Rectangle userBodybox = user.userbody.getBounds();
		Rectangle enemyHeadbox = enemy.aihead.getBounds();
		Rectangle enemyBodybox = enemy.aibody.getBounds();

		if(userHeadbox.intersects(enemyHeadbox) && userBodybox.intersects(enemyBodybox)) {
			return true;	
		} else {
			return false;
		}

	}

	/*This boolean method checks if the user's arm intersects with the enemy's body
	 * 
	 */
	public boolean userhitBody() {

		Rectangle userRightbox = user.userRight.getBounds();
		Rectangle userLeftbox = user.userLeft.getBounds();
		Rectangle enemyBodybox = enemy.aibody.getBounds();

		if(userRightbox.intersects(enemyBodybox) || userLeftbox.intersects(enemyBodybox)) {
			return true;
		} 
		return false;
	}

	//This boolean method checks if the user's arm intersects with the enemy's head
	public boolean userhitHead() {

		Rectangle userRightbox = user.userRight.getBounds();
		Rectangle userLeftbox = user.userLeft.getBounds();
		Rectangle enemyHeadbox = enemy.aihead.getBounds();

		if(userRightbox.intersects(enemyHeadbox) || userLeftbox.intersects(enemyHeadbox)) {
			return true;
		} 
		return false;
	}
	
	//This boolean method checks if the enemy's arm intersects with the user's body (as long as the user is on the ground)
	public boolean enemyhitBody() {
		Rectangle userBodybox = user.userbody.getBounds();
		Rectangle enemyRightbox = enemy.aiRight.getBounds();
		Rectangle enemyLeftbox =enemy.aiLeft.getBounds();
		
		if((enemyRightbox.intersects(userBodybox) || enemyLeftbox.intersects(userBodybox)) && (!user.jumped)) {
			return true;
		}
		return false;
		
	}
	
	//This boolean method checks if the enemy's arm intersects with the user's head (as long as the user is on the ground)
	public boolean enemyhitHead() {
		Rectangle userHeadbox = user.userhead.getBounds();
		Rectangle enemyRightbox = enemy.aiRight.getBounds();
		Rectangle enemyLeftbox =enemy.aiLeft.getBounds();
		
		if((enemyRightbox.intersects(userHeadbox) || enemyLeftbox.intersects(userHeadbox)) && (!user.jumped)) {
			return true;
		}
		return false;
	}
	
}
