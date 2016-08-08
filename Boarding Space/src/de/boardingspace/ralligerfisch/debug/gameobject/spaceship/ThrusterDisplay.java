package de.boardingspace.ralligerfisch.debug.gameobject.spaceship;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static de.boardingspace.ralligerfisch.util.Utilities.lerpColor;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.Renderable;

import de.boardingspace.ralligerfisch.util.TexturedVertex;

public class ThrusterDisplay implements Renderable{
	public static final byte THRUSTER = 0;
	public static final byte STEERING = 1;
	
	private byte displayType;
	private Color CURRENT_COLOR;
	
	private int currentIndicesCount = 0;
	private boolean posNeg = false;
	
	/** CONFIGURABLE PARAMETERS */
	private static final float ELEMENT_WIDTH = 0.01f;
	private static final float ELEMENT_HEIGHT = 0.01f;
	private static final int THRUST_NR_O_ELEMENTS = 10;
	private static final int STEERING_NR_O_ELEMENTS = 6;
	private static final float ELEMENT_BORDER = 0.002f;
	private static final Color MIN_COLOR = new Color(78, 0,0);
	private static final Color MAX_COLOR = new Color(255, 255,0);
	
	
    /** GL VARIABLES */
    private int vaoId = 0;
    private int vboId = 0;
    private int vboiId = 0;
    private int indicesCount = 0;
	
	
	public ThrusterDisplay(byte type,float width, float height,float depth) {
		displayType = type;
		createModel(width,height,depth);
	}
	
	public void render(float percentage){
        if (displayType == THRUSTER)
        	currentIndicesCount = 6*Math.round(THRUST_NR_O_ELEMENTS*percentage);
        else if (displayType == STEERING){
        	this.posNeg = percentage<=0;
        	currentIndicesCount = 6*Math.round((STEERING_NR_O_ELEMENTS/2)*Math.abs(percentage));
        }
        else
        	currentIndicesCount = 0;
		this.render();
	}
	
	@Override
	public void render() {
		glBindVertexArray(vaoId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
         
        // Bind to the index VBO that has all the information about the order of the vertices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
        
        // Draw the vertices
        if(posNeg)
        	glDrawElements(GL_TRIANGLES, currentIndicesCount, GL_UNSIGNED_BYTE, 6*STEERING_NR_O_ELEMENTS/2);
        else
        	glDrawElements(GL_TRIANGLES, currentIndicesCount, GL_UNSIGNED_BYTE, 0);
         
        // Put everything back to default (deselect)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
	}
	
	private void createModel(float width, float height,float depth) {
		TexturedVertex[] vertices = null;
		List<TexturedVertex> vertexList = null;
		byte[] indices = null;
		if(displayType == THRUSTER){
			vertices = new TexturedVertex[4*THRUST_NR_O_ELEMENTS];
			vertexList = new ArrayList<TexturedVertex>(4*THRUST_NR_O_ELEMENTS);
			indices = new byte[6*THRUST_NR_O_ELEMENTS];
			
			float currentStartingHeight = -height/2;
			float currentStartingWidth = width/2 + ELEMENT_BORDER;
			float heightDelta = (height-((THRUST_NR_O_ELEMENTS-1)*ELEMENT_BORDER))/THRUST_NR_O_ELEMENTS;
			for(int i = 0; i<THRUST_NR_O_ELEMENTS;i++){
				CURRENT_COLOR = lerpColor(MIN_COLOR,MAX_COLOR,(float)i/(float)THRUST_NR_O_ELEMENTS);

				vertexList.add(new TexturedVertex(currentStartingHeight,currentStartingWidth,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4));
				vertexList.add(new TexturedVertex(currentStartingHeight+heightDelta,currentStartingWidth,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4+1));
				vertexList.add(new TexturedVertex(currentStartingHeight+heightDelta,currentStartingWidth+ELEMENT_WIDTH,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4+2));
				vertexList.add(new TexturedVertex(currentStartingHeight,currentStartingWidth+ELEMENT_WIDTH,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4+3));

				indices[i*6] = 		(byte) (i*4);
				indices[i*6+1] = 	(byte) (i*4+1);
				indices[i*6+2] = 	(byte) (i*4+2);
				indices[i*6+3] = 	(byte) (i*4+2);
				indices[i*6+4] = 	(byte) (i*4+3);
				indices[i*6+5] = 	(byte) (i*4);
				
				currentStartingHeight += heightDelta+ELEMENT_BORDER;
			}
		}
		if(displayType == STEERING){
			vertices = new TexturedVertex[4*STEERING_NR_O_ELEMENTS];
			vertexList = new ArrayList<TexturedVertex>(4*STEERING_NR_O_ELEMENTS);
			indices = new byte[6*STEERING_NR_O_ELEMENTS];
			
			float currentStartingHeight = -height/2 -ELEMENT_BORDER - ELEMENT_HEIGHT;
			float currentStartingWidth = ELEMENT_BORDER/2;
			float widthDelta = (width-((STEERING_NR_O_ELEMENTS-1)*ELEMENT_BORDER))/STEERING_NR_O_ELEMENTS;
			int offsetV = 4*STEERING_NR_O_ELEMENTS/2;
			int offsetI = 6*STEERING_NR_O_ELEMENTS/2;
			for(int i = 0; i<STEERING_NR_O_ELEMENTS/2;i++){
				CURRENT_COLOR = lerpColor(MIN_COLOR,MAX_COLOR,(float)i/(STEERING_NR_O_ELEMENTS/2f));
				//right
				vertexList.add(new TexturedVertex(currentStartingHeight,currentStartingWidth,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4));
				vertexList.add(new TexturedVertex(currentStartingHeight,currentStartingWidth+widthDelta,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4+1));
				vertexList.add(new TexturedVertex(currentStartingHeight+ELEMENT_HEIGHT,currentStartingWidth+widthDelta,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4+2));
				vertexList.add(new TexturedVertex(currentStartingHeight+ELEMENT_HEIGHT,currentStartingWidth,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,i*4+3));

				indices[i*6] = 		(byte) (i*4);
				indices[i*6+1] = 	(byte) (i*4+1);
				indices[i*6+2] = 	(byte) (i*4+2);
				indices[i*6+3] = 	(byte) (i*4+2);
				indices[i*6+4] = 	(byte) (i*4+3);
				indices[i*6+5] = 	(byte) (i*4);
				currentStartingWidth += widthDelta+ELEMENT_BORDER;
			}
			
			currentStartingWidth = ELEMENT_BORDER/2;
			
			for(int i = 0; i<STEERING_NR_O_ELEMENTS/2;i++){
				CURRENT_COLOR = lerpColor(MIN_COLOR,MAX_COLOR,(float)i/(STEERING_NR_O_ELEMENTS/2f));
				//left
				vertexList.add(new TexturedVertex(currentStartingHeight,-currentStartingWidth,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,offsetV+i*4));
				vertexList.add(new TexturedVertex(currentStartingHeight,-currentStartingWidth-widthDelta,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,offsetV+i*4+1));
				vertexList.add(new TexturedVertex(currentStartingHeight+ELEMENT_HEIGHT,-currentStartingWidth-widthDelta,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,offsetV+i*4+2));
				vertexList.add(new TexturedVertex(currentStartingHeight+ELEMENT_HEIGHT,-currentStartingWidth,-depth,CURRENT_COLOR.getRed()/255f,CURRENT_COLOR.getGreen()/255f,CURRENT_COLOR.getBlue()/255f,CURRENT_COLOR.getAlpha()/255f,0,0,offsetV+i*4+3));

				indices[offsetI+i*6] = 		(byte) (offsetV+i*4);
				indices[offsetI+i*6+1] = 	(byte) (offsetV+i*4+1);
				indices[offsetI+i*6+2] = 	(byte) (offsetV+i*4+2);
				indices[offsetI+i*6+3] = 	(byte) (offsetV+i*4+2);
				indices[offsetI+i*6+4] = 	(byte) (offsetV+i*4+3);
				indices[offsetI+i*6+5] = 	(byte) (offsetV+i*4);
				
				currentStartingWidth += widthDelta+ELEMENT_BORDER;
			}
			
		}

		
		
		vertices = vertexList.toArray(vertices);
        // Put each 'Vertex' in one FloatBuffer
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
                TexturedVertex.elementCount);
        for (int i = 0; i < vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesBuffer.put(vertices[i].getElements());
        }
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
}
