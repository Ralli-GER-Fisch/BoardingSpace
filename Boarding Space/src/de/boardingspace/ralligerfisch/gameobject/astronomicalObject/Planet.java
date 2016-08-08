package de.boardingspace.ralligerfisch.gameobject.astronomicalObject;

import static de.boardingspace.ralligerfisch.game.GameStatus.MODEL_MATRIX_Loc;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.Renderable;
//import org.newdawn.slick.geom.Vector2f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.boardingspace.ralligerfisch.util.TexturedVertex;

public class Planet implements Renderable{
	//private Enum<PlanetType> planetType;
	//private boolean inhabitet;
	//private Vector2f location;
	
	/** MODEL_VARIABLES */
	Integer TextureID = 0;
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
    
    private Matrix4f MODEL_MATRIX = new Matrix4f();
	private FloatBuffer MODEL_MATRIX_BUFFER = BufferUtils.createFloatBuffer(16);
	
	/** CONFIGURABLE PARAMETERS */
	private float PLANET_DEPTH = 35800;
	private Vector3f DEPTH_VECTOR = new Vector3f(0f, 0f, -PLANET_DEPTH);
	private float PLANET_RADIUS = 12700;
    
    public Planet(){
    	setupModel(PLANET_RADIUS, 90);
    }
    public Planet(ReadableColor color){
    	color_R = color.getRed();
    	color_G = color.getGreen();
    	color_B = color.getBlue();
    	color_A = color.getAlpha();
    	setupModel(1, 90);
    }
    
	
	
	public void render(){
		// Bind the texture
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, TextureID);
         
        // Bind to the VAO that has all the information about the vertices
        glBindVertexArray(vaoId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glUniformMatrix4fv(MODEL_MATRIX_Loc, false, MODEL_MATRIX_BUFFER);

         
        // Bind to the index VBO that has all the information about the order of the vertices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
         
        // Draw the vertices
        glDrawElements(GL_TRIANGLE_FAN, indicesCount, GL_UNSIGNED_INT, 0);
         
        // Put everything back to default (deselect)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
		
	}
	
	private void setupModel(float r, int num_segments){
		
		MODEL_MATRIX_BUFFER.clear();
		MODEL_MATRIX.setIdentity();
		Matrix4f.translate(DEPTH_VECTOR,MODEL_MATRIX,MODEL_MATRIX);
		MODEL_MATRIX.store(MODEL_MATRIX_BUFFER);
		MODEL_MATRIX_BUFFER.flip();
		
		TexturedVertex[] vertices = new TexturedVertex[num_segments+1];
		List<TexturedVertex> vertexList = new ArrayList<TexturedVertex>(num_segments+1);
		
		double theta = 2 * Math.PI/num_segments; 
		double c = Math.cos(theta);//precalculate the sine and cosine
		double s = Math.sin(theta);
		double t;

		double x = r;
		double y = 0; 
	    /** CenterPoint */
		vertexList.add(new TexturedVertex((float)0,(float)0,0,color_R,color_G,color_B,color_A,0,0,0));
		/** SurroundPoints */
		for(int i = 0; i < num_segments; i++){
	        vertexList.add(new TexturedVertex((float)x,(float)y,0,color_R,color_G,color_B,color_A,0,0,i+1));
			
			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}
		vertices = vertexList.toArray(vertices);
        // Put each 'Vertex' in one FloatBuffer
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
                TexturedVertex.elementCount);
        int[] indices = new int[vertices.length+1];
        for (int i = 0; i < vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesBuffer.put(vertices[i].getElements());
            indices[i] = vertices[i].getIndex();
        }
        indices[vertices.length] = vertices[1].getIndex();
        verticesBuffer.flip();  
        // OpenGL expects to draw vertices in counter clockwise order by default
        indicesCount = indices.length;
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indicesCount);
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
}
