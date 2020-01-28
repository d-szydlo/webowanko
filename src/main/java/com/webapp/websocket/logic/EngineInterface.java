package com.webapp.websocket.logic;

import com.webapp.websocket.exceptions.*;

public interface EngineInterface {
	
	/**
	 * Glowna metoda odpowiedzialna za ruch po kliknieciu myszki
	 * @param X wspolrzedna X
	 * @param Y wspolrzedna Y
	 * @throws KoRuleViolatedException
	 * @throws CoordinatesOutOfBoundsException
	 * @throws SuicidalMoveException
	 * @throws IntersectionTakenException
	 */
	public void handleMove(int X, int Y) throws KoRuleViolatedException, CoordinatesOutOfBoundsException, SuicidalMoveException, IntersectionTakenException, KoRuleViolatedException;

	/**
	 *  Glowna metoda odpowiedzialna za ruch po kliknieciu przycisku
	 * @param button
	 */
	public void handleButtons(String button);
	
	/**
	 * Metoda odpowiedzialna za ruch bota
	 * @return
	 */
	public Integer[] getBMove();
	
}
