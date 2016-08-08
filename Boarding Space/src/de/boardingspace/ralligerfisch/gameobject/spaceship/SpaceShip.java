package de.boardingspace.ralligerfisch.gameobject.spaceship;


import static de.boardingspace.ralligerfisch.game.GameContainer.focusViewTo;
import static de.boardingspace.ralligerfisch.game.GameStatus.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.Renderable;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.boardingspace.ralligerfisch.debug.gameobject.spaceship.ThrusterDisplay;
import de.boardingspace.ralligerfisch.gameobject.outfit.Outfit;
import de.boardingspace.ralligerfisch.gameobject.outfit.Thruster;
import de.boardingspace.ralligerfisch.gameobject.outfit.UpdateObject;
import de.boardingspace.ralligerfisch.util.TexturedVertex;
import de.boardingspace.ralligerfisch.util.Updateable;
import de.boardingspace.ralligerfisch.util.Utilities;

public class SpaceShip implements Renderable,Updateable{
	
//	private float ENERGY_THRUST_MAX = 200;
//	private float CURRENT_ENERGY_THRUST = 0;
//	private float CURRENT_ENERGY_STEERING = 0;
//	private float ENERGY_STEERING_MAX = 50;
	
	private float MASS_SHIP = 0;
	private float INERTIA_SHIP = 0;
	
	private Vector3f OVERALL_THRUST = new Vector3f();
	private Vector3f OVERALL_TORQUE = new Vector3f();
	private Vector3f LAST_ACCELERATION = new Vector3f();
	private Vector3f CURRENT_ACCELERATION = new Vector3f();
	private Vector3f LAST_ANGULAR_ACCELERATION = new Vector3f();
	private Vector3f CURRENT_ANGULAR_ACCELERATION = new Vector3f();
	private Vector3f CURRENT_VELOCITY = new Vector3f();
	private Vector3f CURRENT_ANGULAR_VELOCITY = new Vector3f();
	//private Matrix4f CURRENT_ROTATION;
	
	private float HEIGHT,WIDTH;
	
	/** GAME VARIABLES */
	private boolean activePlayerShip = false;
	private static Matrix4f TOVIEW_MATRIX = new Matrix4f();
	
	private List<Outfit> outfits = new ArrayList<>();
	
	/** CONFIGURABLE PARAMETERS */
	private float MASS_HULL = 2030000;
	
	//private float VELOCITY_CAP = 0.01f;
	
	
    /** GL VARIABLES */
    private int vaoId = 0;
    private int vboId = 0;
    private int vboiId = 0;
    private int indicesCount = 0;
    
    /** COLOR/TEXTURE VARIABLES */
    private float color_R = 1f;
    private float color_G = 0f;
    private float color_B = 0f;
    private float color_A = 1f;
    
    private float SHIP_DEPTH = 1;

    private Matrix4f MODEL_MATRIX = new Matrix4f();
    private Matrix4f MODEL_MATRIX_ROT = new Matrix4f();
	private FloatBuffer MODEL_MATRIX_BUFFER = BufferUtils.createFloatBuffer(16);
	
	/** UTILVARIABLES */
	private UpdateObject UTIL_UPDATEOBJECT = null;
	//private Thruster UTIL_THRUSTER = null;
	private Integer UTIL_INTEGER = null;
	
	
	/** DEBUG */
	ThrusterDisplay thrusterDisplay;
	ThrusterDisplay steeringDisplay; 
	
	public SpaceShip() {
		HEIGHT = .056f;
		WIDTH = .0087f;
		setupModel(WIDTH,HEIGHT);
	}
	public SpaceShip(ReadableColor color) {
    	color_R = color.getRed()/255;
    	color_G = color.getGreen()/255;
    	color_B = color.getBlue()/255;
    	color_A = color.getAlpha()/255;
		HEIGHT = .056f;
		WIDTH = .0087f;
		thrusterDisplay = new ThrusterDisplay(ThrusterDisplay.THRUSTER,this.WIDTH,this.HEIGHT,this.SHIP_DEPTH);
		steeringDisplay = new ThrusterDisplay(ThrusterDisplay.STEERING,this.WIDTH,this.HEIGHT,this.SHIP_DEPTH);
		
		outfits.add(new Thruster(new Vector3f(-this.HEIGHT/2,this.WIDTH/4,0f), 0f));
		outfits.add(new Thruster(new Vector3f(-this.HEIGHT/2,-this.WIDTH/4,0f), 0f));
		setupModel(WIDTH,HEIGHT);
		calculateMass();
	}
	
	private void calculateMass() {
		MASS_SHIP = MASS_HULL;
		for(Outfit obj:outfits){
			MASS_SHIP += obj.getMass();
		}
		//Inertia of a solid cuboid /May be changed
		INERTIA_SHIP = (float)(MASS_SHIP*(Math.pow(WIDTH, 2)+Math.pow(HEIGHT, 2))/12f);
	}
	public void update(float delta) {
		OVERALL_THRUST.set(0f,0f,0f);
		OVERALL_TORQUE.set(0f,0f,0f);
		
		for(Outfit obj: outfits){
			obj.update(delta);
			UTIL_UPDATEOBJECT = obj.getUpdateobject();
			if(UTIL_UPDATEOBJECT.type == UpdateObject.THRUSTER){
				Vector3f.add(OVERALL_THRUST, UTIL_UPDATEOBJECT.thrust, OVERALL_THRUST);
				Vector3f.add(OVERALL_TORQUE, UTIL_UPDATEOBJECT.torque, OVERALL_TORQUE);
			}
		}
		
		/** using velocity verlet integration*/
		LAST_ACCELERATION.set(CURRENT_ACCELERATION);
		LAST_ANGULAR_ACCELERATION.set(CURRENT_ANGULAR_ACCELERATION);
		
		TOVIEW_MATRIX.translate(new Vector3f(
				-(CURRENT_VELOCITY.x*delta+(0.5f*LAST_ACCELERATION.x*delta*delta)),
				-(CURRENT_VELOCITY.y*delta+(0.5f*LAST_ACCELERATION.y*delta*delta)),
				-(CURRENT_VELOCITY.z*delta+(0.5f*LAST_ACCELERATION.z*delta*delta))));
		
		MODEL_MATRIX_ROT.translate(new Vector3f(
				CURRENT_VELOCITY.x*delta+(0.5f*LAST_ACCELERATION.x*delta*delta),
				CURRENT_VELOCITY.y*delta+(0.5f*LAST_ACCELERATION.y*delta*delta),
				CURRENT_VELOCITY.z*delta+(0.5f*LAST_ACCELERATION.z*delta*delta)));
		MODEL_MATRIX_ROT.rotate(CURRENT_ANGULAR_VELOCITY.x*delta+(0.5f*LAST_ANGULAR_ACCELERATION.x*delta*delta), Utilities.X3f); // will not occure in 2d case
		MODEL_MATRIX_ROT.rotate(CURRENT_ANGULAR_VELOCITY.y*delta+(0.5f*LAST_ANGULAR_ACCELERATION.y*delta*delta), Utilities.Y3f); // will not occure in 2d case
		MODEL_MATRIX_ROT.rotate(CURRENT_ANGULAR_VELOCITY.z*delta+(0.5f*LAST_ANGULAR_ACCELERATION.z*delta*delta), Utilities.Z3f);
		MODEL_MATRIX_BUFFER.clear();
		MODEL_MATRIX_ROT.store(MODEL_MATRIX_BUFFER);
		MODEL_MATRIX_BUFFER.flip();
		
		CURRENT_ACCELERATION.set(
				OVERALL_THRUST.x/MASS_SHIP,
				OVERALL_THRUST.y/MASS_SHIP,
				OVERALL_THRUST.z/MASS_SHIP);
		CURRENT_VELOCITY.set(
				CURRENT_VELOCITY.x+delta*(LAST_ACCELERATION.x+CURRENT_ACCELERATION.x)/2,
				CURRENT_VELOCITY.y+delta*(LAST_ACCELERATION.y+CURRENT_ACCELERATION.y)/2,
				CURRENT_VELOCITY.z+delta*(LAST_ACCELERATION.z+CURRENT_ACCELERATION.z)/2);
		
		CURRENT_ANGULAR_ACCELERATION.set(
				OVERALL_TORQUE.x/INERTIA_SHIP,
				OVERALL_TORQUE.y/INERTIA_SHIP,
				OVERALL_TORQUE.z/INERTIA_SHIP);
		CURRENT_ANGULAR_VELOCITY.set(
				CURRENT_ANGULAR_VELOCITY.x+delta*(LAST_ANGULAR_ACCELERATION.x+CURRENT_ANGULAR_ACCELERATION.x),
				CURRENT_ANGULAR_VELOCITY.y+delta*(LAST_ANGULAR_ACCELERATION.y+CURRENT_ANGULAR_ACCELERATION.y),
				CURRENT_ANGULAR_VELOCITY.z+delta*(LAST_ANGULAR_ACCELERATION.z+CURRENT_ANGULAR_ACCELERATION.z));
		
		
		// Update ViewMatrix if current Ship is active Ship
		if(activePlayerShip){
			focusViewTo(MODEL_MATRIX_ROT.m30,MODEL_MATRIX_ROT.m31);
		}
	}
	
	public void render(){
		glBindVertexArray(vaoId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glUniformMatrix4fv(MODEL_MATRIX_Loc, false, MODEL_MATRIX_BUFFER);
         
        // Bind to the index VBO that has all the information about the order of the vertices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
         
        // Draw the vertices
        glDrawElements(GL_LINE_LOOP, indicesCount, GL_UNSIGNED_BYTE, 0);
         
        // Put everything back to default (deselect)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        
        
        if(DEBUG)
        	renderEngineThrust();
	}
	
	private void renderEngineThrust(){
//		thrusterDisplay.render(CURRENT_ENERGY_THRUST/ENERGY_THRUST_MAX);
//		steeringDisplay.render(CURRENT_ENERGY_STEERING/ENERGY_STEERING_MAX);
	}
	
	private void setupModel(float width,float height){
		TOVIEW_MATRIX.setIdentity();
		MODEL_MATRIX_BUFFER.clear();
		MODEL_MATRIX.setIdentity().store(MODEL_MATRIX_BUFFER);
		MODEL_MATRIX_BUFFER.flip();
		
		TexturedVertex[] vertices = new TexturedVertex[4];
		List<TexturedVertex> vertexList = new ArrayList<TexturedVertex>(4);
		vertexList.add(new TexturedVertex(height/2,width/2,-SHIP_DEPTH,color_R,color_G,color_B,color_A,0,0,0));
		vertexList.add(new TexturedVertex(height/2,-width/2,-SHIP_DEPTH,color_R,color_G,color_B,color_A,0,0,1));
		vertexList.add(new TexturedVertex(-height/2,-width/2,-SHIP_DEPTH,color_R,color_G,color_B,color_A,0,0,2));
		vertexList.add(new TexturedVertex(-height/2,width/2,-SHIP_DEPTH,color_R,color_G,color_B,color_A,0,0,3));

		
		
		vertices = vertexList.toArray(vertices);
        // Put each 'Vertex' in one FloatBuffer
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
                TexturedVertex.elementCount);
        for (int i = 0; i < vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesBuffer.put(vertices[i].getElements());
        }
        byte[] indices = new byte[] {0,1,2,2,3,0};
        verticesBuffer.flip();  
        // OpenGL expects to draw vertices in counter clockwise order by default
        indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
         
        // Create a new Vertex Array Object in memory and select it (bind)
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
         
        // Create a new Vertex Buffer Object in memory and select it (bind)
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
         
        // Put the position coordinates in attribute list 0
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL_FLOAT, 
                false, TexturedVertex.stride, TexturedVertex.positionByteOffset);
        // Put the color components in attribute list 1
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL_FLOAT, 
                false, TexturedVertex.stride, TexturedVertex.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL_FLOAT, 
                false, TexturedVertex.stride, TexturedVertex.textureByteOffset);
         
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
         
        // Deselect (bind to 0) the VAO
        glBindVertexArray(0);
         
        // Create a new VBO for the indices and select it (bind) - INDICES
        vboiId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	
	public void updateActiveShip(float delta){
		for(Outfit obj:outfits){
			KEY_MAP.forEach((key,value)->{
				if(value){
					UTIL_INTEGER = obj.getKeyFunctionMap().get(key);
					if(UTIL_INTEGER != null)
						obj.getFunktionList().get(UTIL_INTEGER).accept(delta);
				}
			});
		}
	}
	
	public Matrix4f getModelMatrix(){
		return MODEL_MATRIX;
	}
	public boolean isActivePlayerShip() {
		return activePlayerShip;
	}
	public void setActivePlayerShip(boolean activePlayerShip) {
		this.activePlayerShip = activePlayerShip;
	}
}
