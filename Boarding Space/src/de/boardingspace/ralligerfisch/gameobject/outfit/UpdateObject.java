package de.boardingspace.ralligerfisch.gameobject.outfit;

import org.lwjgl.util.vector.Vector3f;

public class UpdateObject {
	/** TYPES */
	public static final byte THRUSTER = 0;
	
	/** Variables */
	public byte type;
	public Vector3f thrust;
	public Vector3f torque;
	public float mass;
	
	public UpdateObject(byte type) {
		this.type = type;
	}
}
