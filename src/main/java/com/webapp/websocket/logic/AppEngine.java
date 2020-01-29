package com.webapp.websocket.logic;


import java.awt.Color;
import java.util.ArrayList;

import com.webapp.websocket.exceptions.*;

/**
 * 
 * @author Oliwier Kaszyca & Dominika Szydlo
 *
 */
public class AppEngine implements EngineInterface {

	
	/**
	 * rozmiar planszy
	 */
	private int boardSize;
	
	/**
	 * obecna plansza z kamieniami
	 */
	public Stone[][] currentBoard;
	
	/**
	 *  poprzednia plansza z kamieniami
	 */
	private Stone[][] previousBoard;
	
	/**
	 * plansza na podstawie ktorej sprawdzane jest KO
	 */
	private Stone[][] koBoard;
	
	/**
	 * zmienna przechowująca informacje o turze danego gracza
	 */
	private boolean blackTurn;

	/**
	 * licznik spasowan
	 */
	private int passCounter;
	
	/**
	 * licznik tur
	 */
	private int turnCounter;
	
	/**
	 * fabryka kamieni
	 */
	private StoneFactory factory;
	
	/**
	 * zmiany ktore wystapily na planszy
	 */
	private String changes;
	
	/**
	 * licznik zmian
	 */
	private int changesCounter;
	
	/**
	 * zmienna z ostatecznym wynikiem
	 */
	private String finalScore = "";

	private boolean koHappened = false;
	
	/**
	 * Konstruktor silnika
	 * @param boardSize rozmiar plan
	 */
	public AppEngine(int boardSize) {
		
		this.boardSize = boardSize;
		this.currentBoard = new Stone[boardSize][boardSize];
		this.koBoard = new Stone[boardSize][boardSize];
		this.previousBoard = new Stone[boardSize][boardSize];
		blackTurn = true;
		passCounter = 0;
		turnCounter = 0;
		factory = new ConcreteStoneFactory();
		changes = "";
		
	}
	
	/**
	 * Glowna metoda wykonujaca ruch po kliknieciu myszka
	 */
	public void handleMove(int X, int Y) throws KoRuleViolatedException, CoordinatesOutOfBoundsException, SuicidalMoveException, IntersectionTakenException {

		if(!koHappened)
		prepareArrays();
		
		if(checkIfInBounds(X, Y)) {
			if (checkIfTaken(X, Y)) {
				throw new IntersectionTakenException();
			}
			else {
					addStone(X, Y);
				if (checkIfKo(X, Y)) {
					removeStone(X, Y);
					koHappened = true;
					throw new KoRuleViolatedException();
				}
				else {
					koHappened = false;
					if (checkIfStrangles(X, Y)) {
						removeStrangledStones(X, Y);
						changeTurn();
						passCounter = 0;
					} 
				
					else {
					
						if (checkIfSuicidal(X, Y)) {
							removeStone(X, Y);
							throw new SuicidalMoveException();
						} 
						else {
							changeTurn();
							passCounter = 0;

						}
					}
				}
			}
		}
		else
			throw new CoordinatesOutOfBoundsException();
		}
	
	
	/**
	 * Metoda przygotowujaca plansze
	 */
	public void prepareArrays() {
		
		if (turnCounter > 1) {
			switchArrays(koBoard,previousBoard);
		}
		if (turnCounter > 0) {
			switchArrays(previousBoard,currentBoard);
		}
	}
	
	/**
	 * Metoda sprawdzajaca czy podane wspolrzedne sa w granicy planszy
	 * @param X wspolrzedna X
	 * @param Y wspolrzedna Y
	 * @return true, gdy jest w granicach planszy
	 */
	public boolean checkIfInBounds(int X, int Y) {
		boolean outcome = false;
		if((X >=0 && Y >=0) && (X < boardSize && Y < boardSize)) outcome = true;
		return outcome;
	}

	/**
	 * Metoda sprawdzajace zasade KO
	 * @param X wspolrzedna X
	 * @param Y wspolrzedna Y
	 * @return true, gdy jest naruszona zasada
	 */
	public boolean checkIfKo(int X, int Y) {
		if (turnCounter < 2) {
			return false;
		}

		boolean outcome = false;

		if(currentBoard[X][Y] != null && koBoard[X][Y] != null) {
			if(currentBoard[X][Y].getColor() == koBoard[X][Y].getColor()) {
				
				outcome = true;
				
			}
		}
		
		return outcome;
	}

	/**
	 * Metoda sprawdzajaca czy ruch jest samobojczy
	 * @param X wspolrzedna X
	 * @param Y wspolrzedna Y
	 * @return true, gdy ruch jest samobojczy
	 */
	public boolean checkIfSuicidal(int X, int Y) {

		boolean outcome;
		if (checkIfStrangledDomki(X,Y)) {

			outcome = true;
		}
		else {
			outcome = false;
		}

		return outcome;
	}

	/**
	 * Metoda sprawdzajaca czy dane pole jest sasiadem checkIfNeighbour
	 * @param X wspolrzedna X pola pierwszego
	 * @param Y wspolrzedna Y pola pierwszego
	 * @param A wspolrzedna X pola drugiego
	 * @param B wspolrzedna Y pola drugiego
	 * @return true, jesli sa sasiadami
	 */
	public boolean checkIfNeighbour(int X, int Y, int A, int B) {
		boolean outcome = false;
		int xDif = Math.abs(X-A);
		int yDif = Math.abs(Y-B);
		if ((xDif ==1 && yDif ==0) || (xDif == 0 && yDif==1)) outcome = true;
		return outcome;
	}
	
	/**
	 *  Sprawdzanie skladu lancucha
	 * @param X wspolrzedna X
	 * @param Y wspolrzedna Y
	 * @return lista z lancuchem
	 */
	public ArrayList<Integer> getDomkaChain(int X, int Y){
		ArrayList<Integer> domkaChain = new ArrayList<>();
		Color color = currentBoard[X][Y].getColor();
		ArrayList<Integer> colorChain = new ArrayList<>();
		domkaChain.add(X);
		domkaChain.add(Y);
		currentBoard[X][Y].ifChecked = true;
		for(int i=0; i< boardSize;i++) {
			for(int j=0;j<boardSize;j++) {
				if (currentBoard[j][i] != null && currentBoard[j][i].getColor() == color) {
					colorChain.add(j);
					colorChain.add(i);
				}
			}
		}
		for (int k=0;k<5;k++) {
			for(int i=0; i< colorChain.size()/2;i++) {
				if (!currentBoard[colorChain.get(2*i)][colorChain.get(2*i+1)].ifChecked) {
				for(int j=0; j<domkaChain.size()/2;j++) {
					if (checkIfNeighbour(colorChain.get(2*i),colorChain.get(2*i+1),domkaChain.get(2*j),domkaChain.get(2*j+1))) {
						domkaChain.add(colorChain.get(2*i));
						domkaChain.add(colorChain.get(2*i+1));
						currentBoard[colorChain.get(2*i)][colorChain.get(2*i+1)].ifChecked = true;
						break;
					}
				}}
			}
		}
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				
				if(currentBoard[i][j] != null)
					currentBoard[i][j].setChecked(false);
				
			}
		}
		return domkaChain;
	}
	
	/**
	 * Metoda sprawdzajaca czy dany pionek jest martwy
	 * @param X
	 * @param Y
	 * @return true jesli jest martwy
	 */
	public boolean checkIfStrangledDomki(int X, int Y) {
		ArrayList<Integer> domkaChain = getDomkaChain(X,Y);
		boolean outcome = true;
		for (int i=0;i<domkaChain.size()/2;i++) {
			if (checkIfGotBreaths(domkaChain.get(2*i),domkaChain.get(2*i+1))) outcome = false;
		}
		return outcome;
	}
	
	/**
	 * Metoda sprawdzajaca czy dany pionek ma oddechy
	 * @param X
	 * @param Y
	 * @return true jesli ma oddechy
	 */
	public boolean checkIfGotBreaths(int X, int Y) {
		boolean outcome = false;
		ArrayList<Integer> coords = getCoordsToCheck(X,Y);
		

		for (int i=0;i<coords.size()/2;i++) {
	
			if (currentBoard[coords.get(i*2)][coords.get(2*i+1)] == null) {
				outcome = true;
			}
		}

		return outcome;
	}
	
	/**
	 * Metoda zwracajaca sasiednie pola
	 * @param X
	 * @param Y
	 * @return lista sasiadow
	 */
	public ArrayList<Integer> getCoordsToCheck(int X, int Y){
		ArrayList<Integer> coords = new ArrayList<Integer>();
		if (X-1>=0) {
			coords.add(X-1);
			coords.add(Y);
		}
		if (X+1 < boardSize) {
			coords.add(X+1);
			coords.add(Y);
		}
		if (Y-1>= 0) {
			coords.add(X);
			coords.add(Y-1);
		}
		if (Y+1 < boardSize) {
			coords.add(X);
			coords.add(Y+1);
		}
		
		return coords;
	}

	/**
	 * Metoda dodajaca kamienie do tablicy
	 * @param X
	 * @param Y
	 */
	public void addStone(int X, int Y) {
		
		if(blackTurn) {
			
	    	Stone blackStone = factory.getStone("Black");
			currentBoard[X][Y] = blackStone; 
				
		} else {
			
			Stone whiteStone = factory.getStone("White");
			currentBoard[X][Y] = whiteStone;
			
		}
	}
	
	/**
	 * Metoda przygotowujaca kamienie do usuniecia
	 * @param X
	 * @param Y
	 * @return lista koordynatow kamieni
	 */
	public ArrayList<Integer> getCoordsToRemove(int X, int Y){
		ArrayList<Integer> coords = new ArrayList<Integer>();
		for(int i=0;i<boardSize;i++) {
			for(int j=0;j<boardSize;j++) {
				if(!(i == X && j == Y) && currentBoard[i][j] != null) {
					if(checkIfStrangledDomki(i,j)) {
						coords.add(i);
						coords.add(j);
					}
				}
			}
		}
		return coords;
	}

	/**
	 * Metoda usuwajaca kamienie
	 * @param X
	 * @param Y
	 */
	public void removeStone(int X, int Y) {
		
		currentBoard[X][Y] = null;

	}

	/**
	 * Metoda przygotowujaca i usuwajaca martwe kamienie
	 * @param X
	 * @param Y
	 */
	public void removeStrangledStones(int X, int Y) {
		
		ArrayList<Integer> coords = getCoordsToRemove(X,Y);
		
		
		for(int i=0;i<coords.size()/2;i++) {
		 
			removeStone(coords.get(2*i), coords.get(2*i+1));
		}
	}

	/**
	 * Metoda sprawdzajaca czy dany pionek moze bic
	 * @param X
	 * @param Y
	 * @return true jesli ma bicie
	 */
	public boolean checkIfStrangles(int X, int Y) {
		
		Color color = currentBoard[X][Y].getColor();
		ArrayList<Integer> coords = getCoordsToCheck(X,Y);
		for (int i=0;i<coords.size()/2;i++) {
			int newX = coords.get(2*i);
			int newY = coords.get(2*i+1);
			if (currentBoard[newX][newY] != null && currentBoard[newX][newY].getColor() != color) {
				if (checkIfStrangledDomki(newX,newY)) {

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Metoda sprawdzajaca czy pole jest zajete
	 * @param X
	 * @param Y
	 * @return true jesli pole jest zajete
	 */
	public boolean checkIfTaken(int X, int Y) {
		if (currentBoard[X][Y]==null) {
			return false;
		}
		else return true;
	}

	/**
	 * Glowna metoda wykonujaca dzialanie po wcisnietym przycisku
	 */
	@Override
	public void handleButtons(String button) {
		passCounter += 1;
		changeTurn();
		if (passCounter == 2) {
			setFinalScore();
		}
	}
	
	/**
	 * Metoda ustawiajaca ostateczny wynik, bedacy stringiem skladajacym sie z koloru i przewagi pkt
	 */
	public void setFinalScore() {
		
		int blackPoints = getScore(Color.black);
		int whitePoints = getScore(Color.white);
		
		int delta;
		
		if(blackPoints > whitePoints) {
			
			delta = blackPoints - whitePoints;
					finalScore += "Czarny " + delta;
				
		}
		else if(blackPoints < whitePoints) {
			
			delta = whitePoints - blackPoints;
			finalScore += "Bialy " + delta;
		}
		else 
			finalScore = "remis";
			
	}
	
	/**
	 * Metoda zmieniajaca tablice
	 * @param array1
	 * @param array2
	 */
	public void switchArrays(Stone[][] array1, Stone[][] array2) {
		
		for(int i = 0 ; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				array1[i][j] = array2[i][j];
			}
		}
	}
	
	/**
	 * Punkty danego gracza
	 * @param color kolor gracza
	 * @return int punkty
	 */
	private int getScore(Color color) {
		
		int score = 0;
		boolean isEye = true; 
		ArrayList<Integer> coords = new ArrayList<Integer>();
		
		
		for(int i = 0; i<boardSize; i++) {
			for(int j = 0; j<boardSize; j++) {
				
				coords.clear();
				
				if(currentBoard[i][j] != null){
					
					if(currentBoard[i][j].getColor() == color) {
						
						score+=1;
					}	
				}
				else
				{
					coords = getCoordsToCheck(i,j);
					 isEye = true;
					for(int x = 0; x<coords.size()/2; x++) {
						
						if(currentBoard[coords.get(2*x)][coords.get(2*x+1)] == null ) {
							isEye = false;
						}
						else if (currentBoard[coords.get(2*x)][coords.get(2*x+1)] != null && currentBoard[coords.get(2*x)][coords.get(2*x+1)].getColor() != color)
							isEye = false;
					
						
					}
					if(isEye)
						score+=1;
				}
						
			}
			
		}
	
		return score;	
	
	}
	
	/**
	 * Metoda zmieniajaca ture
	 */
	public void changeTurn() {
		if (blackTurn) {
			blackTurn = false;
		}
		else {
			blackTurn = true;
		}
		turnCounter++;
	}
	
	
	/**
	 * Metoda resetujaca string ze zmianami i ostatecznym wynikiem	
	 */
	public void resetChanges() {
		changes = "";
		finalScore = "";
	}
	
	/**
	 * Metoda sprawdzajaca zmiany
	 * @return string ze zmianami 
	 */
	public String getChanges(){
		for (int i=0;i<boardSize;i++) {
			for (int j=0;j<boardSize;j++) {
				if(currentBoard[i][j] == null && previousBoard[i][j] != null) {
					changes += Integer.toString(i);
					changes += " ";
					changes += Integer.toString(j);
					changes += " ";
				}
			}
		}
		return this.changes;
	}

	/**
	 * Metoda odpowiedzialna za ruch bota
	 */
	public Integer[] getBMove() {
		int delta = getScore(Color.white) - getScore(Color.black);
		Integer[] coords = new Integer[2];
		
		for(int i=0;i<boardSize;i++) {
			for (int j=0;j<boardSize;j++) {
				
				if (!checkIfTaken(i,j)) {
					
					addStone(i,j);
					if(!checkIfSuicidal(i,j) && !checkIfKo(i,j)) {
							coords[0] = i;
							coords[1] = j;
						}
					removeStone(i,j);
					break;
					}
		
				}
				
			}
		
		for(int i=0;i<boardSize;i++) {
			for (int j=0;j<boardSize;j++) {
				
				if (!checkIfTaken(i,j)) {
						addStone(i,j);
						if(!checkIfSuicidal(i,j) && !checkIfKo(i,j)) {
							int tmp = getScore(Color.white) - getScore(Color.black);
							if (tmp > delta) {
								coords[0] = i;
								coords[1] = j;
								delta = tmp;
							}
						}
						
						removeStone(i,j);
					}
				}
			}
		return coords;
	}
	
	
	public int getChangesCounter() {
		return changesCounter;
	}

	public void setChangesCounter(int changesCounter) {
		this.changesCounter = changesCounter;
	}

	public String getFinalScore() {
		return finalScore;
	}

	public int getTurnCounter() {

		return this.turnCounter;
	}

	public int getPassCounter() {
		return passCounter;
	}

	public void reloadEngine(){
		this.currentBoard = new Stone[boardSize][boardSize];
		this.koBoard = new Stone[boardSize][boardSize];
		this.previousBoard = new Stone[boardSize][boardSize];
		blackTurn = true;
		passCounter = 0;
		turnCounter = 0;


	}
	
}
