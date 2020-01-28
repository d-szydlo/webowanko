package com.webapp.websocket.logic;

import java.awt.Color;


/**
 * 
 * @author Oliwier Kaszyca & Dominika Szydło
 *
 * Klasa po której dziedziczą kamienie
 */
public class Stone  {

	/**
	 * Kolor kamienia
	 */
	Color stoneColor;
	
	/**
	 * Czy kamień został sprawdzony (używane w systemie bicia)
	 */
	boolean ifChecked;
	
	boolean ifChain;

	
	
	public Color getColor() {
		
		return stoneColor;
	}
	
	public void setChecked(boolean ifChecked) {
		
		this.ifChecked = ifChecked;
		
	}
	
	public boolean getChecked() {
		
		return ifChecked;
	}
	
	
}
