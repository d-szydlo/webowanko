package com.webapp.websocket.logic;

/**
 * 
 * @author Oliwier Kaszyca & Dominika Szydło
 * 
 * Interfejs fabryki kamieni
 */
public interface StoneFactory {
	
	/**
	 * Metoda tworząca kamienie
	 * @param stoneType "White" lub "Black"
	 * @return odpowiedni kamień 
	 */
	Stone getStone(String stoneType);

}
