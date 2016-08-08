package de.boardingspace.ralligerfisch.util;


public class Texture {
	private byte[][][] textureData;
	private Dimension<Integer> resolution;

	/** CONSTRUCTORS AREA */
	public Texture(Dimension<Integer> resolution){
		this.resolution = resolution;
		try {
			textureData = new byte[this.resolution.getValue(0)][this.resolution.getValue(1)][this.resolution.getValue(2)];
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Improper Dimension for Texture Object");
			System.err.println("Creating Texture of size 512x512x3");
			textureData = new byte[512][512][3];
		}
	}
	public Texture(int xSize, int ySize, int nrOColorChannels){
		this(new Dimension<Integer>(xSize,ySize,nrOColorChannels));
	}
	/** FUNCTIONS AREA */
	
	
	/** GETTER/SETTER AREA */
}
