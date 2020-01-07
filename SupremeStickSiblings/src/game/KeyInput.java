package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

	private static final boolean[] keys = new boolean[256];
	private static final boolean[] lastKey = new boolean[256];


	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	public static boolean isPressed(int keyCode) {
		return keys[keyCode];
	}
	public static void update() {
		for(int i=0;i<256;i++) {
			lastKey[i] = keys[i];
		}
	}
	public static boolean wasReleased(int keyCode) { 
		return !isPressed(keyCode) && lastKey[keyCode]; 
	}

}
