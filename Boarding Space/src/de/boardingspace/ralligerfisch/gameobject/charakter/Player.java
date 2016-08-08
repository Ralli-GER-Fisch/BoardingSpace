package de.boardingspace.ralligerfisch.gameobject.charakter;

import org.lwjgl.util.vector.Vector2f;

import de.boardingspace.ralligerfisch.gameobject.spaceship.SpaceShip;
import de.boardingspace.ralligerfisch.util.Updateable;

public class Player extends Charakter implements Updateable {
	
	
	private SpaceShip activeShip;
	public Player(String name,SpaceShip activeShip) {
		this.name = name;
		this.activeShip = activeShip;
		activeShip.setActivePlayerShip(true);
	}
	
	public void move(Vector2f transpose, Vector2f rotate){
		//activeShip.move(transpose, rotate);		
	}

	public void update(float delta) {
			activeShip.updateActiveShip(delta);
	}
}
