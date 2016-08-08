package de.boardingspace.ralligerfisch.gameobject.outfit;

import java.util.HashMap;
import java.util.function.Consumer;

import de.boardingspace.ralligerfisch.util.Updateable;

public abstract class Outfit implements Updateable{
	private float mass = 100;
	private UpdateObject updateObject;
	public UpdateObject getUpdateobject(){return updateObject;}
	public void setUpdateobject(UpdateObject updateObject){this.updateObject = updateObject;}
	public float getMass() {return mass;}
	public void setMass(float mass) {this.mass = mass;}
	/** ABSTRACTS */
	public abstract Class<?> getType();
	public abstract HashMap<Integer, Consumer<Float>> getFunktionList();
	public abstract HashMap<Integer, String> getFunktionIdNames();
	public abstract HashMap<Integer, Integer> getKeyFunctionMap(); 
}
