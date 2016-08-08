package de.boardingspace.ralligerfisch.game;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.util.glu.GLU.gluErrorString;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static de.boardingspace.ralligerfisch.game.GameStatus.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.util.Color;
import org.lwjgl.util.Renderable;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.opengl.PNGDecoder;

import de.boardingspace.ralligerfisch.gameobject.astronomicalObject.Planet;
import de.boardingspace.ralligerfisch.gameobject.charakter.Player;
import de.boardingspace.ralligerfisch.gameobject.spaceship.SpaceShip;
//import de.boardingspace.ralligerfisch.generators.util.Noise;
//import de.boardingspace.ralligerfisch.generators.util.NoiseGenerator;
//import de.boardingspace.ralligerfisch.util.Dimension;
//import de.boardingspace.ralligerfisch.util.NDimensionalArray;
import de.boardingspace.ralligerfisch.util.Updateable;

public class GameContainer {
	private long window;

	/** The time the last frame was rendered */
	protected long lastFrame;
	/** The last time the FPS recorded */
	protected long lastFPS;
	/** The last recorded FPS */
	protected int recordedFPS;
	/** The current count of FPS */
	protected int fps;
	/** The current delta */
	protected float delta;

	private static long variableYieldTime, lastTime;
	
	/** The FPS we want to lock to */
	protected int targetFPS = 60;
	/** True if we should show the fps */
	// private boolean showFPS = true;
	/** The minimum logic update interval */
	protected long minimumLogicInterval = 1;
	/** The stored delta */
	protected long storedDelta;
	/** The maximum logic update interval */
	protected long maximumLogicInterval = 0;

	/** True if vsync has been requested */
	protected boolean vsync;
	/** Smoothed deltas requested */
	protected boolean smoothDeltas = true;

	public GameContainer() {
		lastFrame = System.nanoTime();
		delta = 0.0f;
	}

	public void run() {
		System.out.println("Starting up subatomic engines!");

		try {
			init();
			loop();

			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
													// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be
													// resizable

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		// Enable Multisampling
		glfwWindowHint(GLFW_SAMPLES, 4);
		
		

		// Create the window
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Boarding Space", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// TODO: KeyCallbacks
		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this
														// in our rendering loop
			if (action == GLFW_PRESS) {
				KEY_MAP.replace(key, true);
			}
			if (action == GLFW_RELEASE) {
				KEY_MAP.replace(key, false);
			}
		});

		glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
			onResize(width, height);
		});

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			CURSOR_X_POS = xpos;
			CURSOR_Y_POS = ypos;
		});

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
			CURSOR_X_SCROLL_OFFSET += xoffset;
			CURSOR_Y_SCROLL_OFFSET += yoffset;
		});

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			MOUSE_BUTTON = button;
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		ASPECT_RATIO = vidmode.width() / vidmode.height();
		SCENE_WIDTH = ASPECT_RATIO * SCENE_HEIGHT;
		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - WINDOW_WIDTH) / 2, (vidmode.height() - WINDOW_HEIGHT) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		createCapabilities();
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		initGL();

		initTests();
	}

	private void initGL() {
		setupShaders();

		PROJECTION_MATRIX_Loc = glGetUniformLocation(ID_SHADER_PROGRAM, "projection");
		if (PROJECTION_MATRIX_Loc == -1) {
			System.err.println("Failed setting up the uniform 'projection' for shader " + ID_SHADER_PROGRAM);
			throw new RuntimeException();
		}
		PROJECTION_MATRIX.setIdentity();
		PROJECTION_MATRIX_BUFFER.clear();
		PROJECTION_MATRIX.store(PROJECTION_MATRIX_BUFFER);
		PROJECTION_MATRIX_BUFFER.flip();

		MODEL_MATRIX_Loc = glGetUniformLocation(ID_SHADER_PROGRAM, "model");
		if (MODEL_MATRIX_Loc == -1) {
			System.err.println("Failed setting up the uniform 'model' for shader " + ID_SHADER_PROGRAM);
			throw new RuntimeException();
		}

		VIEW_MATRIX_Loc = glGetUniformLocation(ID_SHADER_PROGRAM, "view");
		if (VIEW_MATRIX_Loc == -1) {
			System.err.println("Failed setting up the uniform 'view' for shader " + ID_SHADER_PROGRAM);
			throw new RuntimeException();
		}
		VIEW_MATRIX.setIdentity();
		VIEW_MATRIX_BUFFER.clear();
		VIEW_MATRIX.store(VIEW_MATRIX_BUFFER);
		VIEW_MATRIX_BUFFER.flip();

		onResize(WINDOW_WIDTH, WINDOW_HEIGHT);

	}

	/** TESTING VARIABLES */
	Integer TextureID = 0;

	private void initTests() {
		// createCapabilities();

		// glViewport(0, 0, 800, 600);

		// Long seed = new Long(1);
		// NoiseGenerator nG = new NoiseGenerator(seed);
		// NDimensionalArray<Double> texture = nG.generateFBM(new
		// Dimension<Integer>(800,600), 0.5f, 2f, 5, new Noise(seed));
		// ByteBuffer buf = texture.toByteArray();
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream("assets/pngtest.png");
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);

			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();

			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.RGBA);
			buf.flip();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		buf.flip();

		int textureid = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureid);

		// All RGB bytes are aligned to each other and each component is 1 byte
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		// glTexImage2D(GL_TEXTURE_2D, 0, GL_INTENSITY8, tWidth, tHeight, 0,
		// GL_LUMINANCE, GL_UNSIGNED_BYTE, buf);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, tWidth, tHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glGenerateMipmap(GL_TEXTURE_2D);

		// Setup the ST coordinate system
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		TextureID = textureid;

		RENDERABLE_OBJECTS.add(new Planet());
		SpaceShip ship = new SpaceShip(Color.GREEN);
		RENDERABLE_OBJECTS.add(ship);
		MAIN_PLAYER = new Player("Jorgo", ship);
		UPDATEABLE_OBJECTS.add(MAIN_PLAYER);
		UPDATEABLE_OBJECTS.add(ship);
		// this.exitOnGLError("setupQuad");

	}
	
	public static void focusViewTo(float x, float y){
		VIEW_MATRIX.m30 = -x;
		VIEW_MATRIX.m31 = -y;
	}

	private void loop() {
		long now = System.nanoTime();
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		// createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.1f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			sync(targetFPS);
			updateFPS();
			if (smoothDeltas) {
				if (getFPS() != 0) {
					delta = getDelta();//((now - lastFrame)) / getFPS();
					//lastFrame = now;
				}
			} else {
				delta = 0;
				lastFrame = now;
			}
			update(delta);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// FRAMEBUFFER
			render(delta);
		}
	}

	private void render(float delta) {

		glUseProgram(ID_SHADER_PROGRAM);
		glUniformMatrix4fv(PROJECTION_MATRIX_Loc, false, PROJECTION_MATRIX_BUFFER);
		glUniformMatrix4fv(VIEW_MATRIX_Loc, false, VIEW_MATRIX_BUFFER);

		for (Renderable obj : RENDERABLE_OBJECTS)
			obj.render();

		glUseProgram(0);

		glfwSwapBuffers(window); // swap the color buffers

	}

	private void update(float delta) {
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
		// TODO: Modify Game Logic.

		/** Modify Zoom */
		if (CURSOR_Y_SCROLL_OFFSET != 0d) {
			CURSOR_Y_SCROLL_OFFSET_TMP = CURSOR_Y_SCROLL_OFFSET;
			ZOOM_VALUE -= FAC_ZOOM * CURSOR_Y_SCROLL_OFFSET_TMP;
			ZOOM_VALUE = ZOOM_VALUE < ZOOM_MIN ? ZOOM_MIN : (ZOOM_VALUE > ZOOM_MAX ? ZOOM_MAX : ZOOM_VALUE);
			CURSOR_Y_SCROLL_OFFSET -= CURSOR_Y_SCROLL_OFFSET_TMP;
			CURSOR_Y_SCROLL_OFFSET_TMP = 0d;
		}

		/** MODIFY PLAYER POSITION */
		// if(KEY_UP == GLFW_KEY_DOWN){
		//
		// }
		// if(KEY_DOWN == GLFW_KEY_DOWN){
		//
		// }
		// if(KEY_LEFT == GLFW_KEY_DOWN){
		//
		// }
		// if(KEY_RIGHT == GLFW_KEY_DOWN){
		//
		// }

		for (Updateable obj : UPDATEABLE_OBJECTS) {
			obj.update(delta);
		}
		updateViewMatrix();
	}
	
	private void updateViewMatrix(){
		VIEW_MATRIX.m32 = ZOOM_VALUE;
		System.out.println(VIEW_MATRIX);
		VIEW_MATRIX_BUFFER.clear();
		VIEW_MATRIX.store(VIEW_MATRIX_BUFFER);
		VIEW_MATRIX_BUFFER.flip();
	}
	
	private void setupShaders() {
		// Load the vertex shader
		ID_SHADER_VERTEX = this.loadShader("Shaders/TexturedShader/default_textured.vert", GL_VERTEX_SHADER);
		// Load the fragment shader
		ID_SHADER_FRAGMENT = this.loadShader("Shaders/TexturedShader/default_textured.frag", GL_FRAGMENT_SHADER);

		// Save ShaderName
		NAME_SHADER_PROGRAM = "default_textured";

		// Create a new shader program that links both shaders
		ID_SHADER_PROGRAM = glCreateProgram();
		glAttachShader(ID_SHADER_PROGRAM, ID_SHADER_VERTEX);
		glAttachShader(ID_SHADER_PROGRAM, ID_SHADER_FRAGMENT);

		// Position information will be attribute 0
		glBindAttribLocation(ID_SHADER_PROGRAM, 0, "in_Position");
		// Color information will be attribute 1
		glBindAttribLocation(ID_SHADER_PROGRAM, 1, "in_Color");
		// Textute information will be attribute 2
		glBindAttribLocation(ID_SHADER_PROGRAM, 2, "in_TextureCoord");

		glLinkProgram(ID_SHADER_PROGRAM);
		glValidateProgram(ID_SHADER_PROGRAM);

		this.exitOnGLError("setupShaders");
	}

	private int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);

		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Could not compile shader.");
			System.err.print(glGetShaderInfoLog(shaderID));
			System.exit(-1);
		}

		this.exitOnGLError("loadShader");

		return shaderID;
	}

	private void exitOnGLError(String errorMessage) {
		int errorValue = glGetError();

		if (errorValue != GL_NO_ERROR) {
			String errorString = gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);

			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
			glfwTerminate();
			glfwSetErrorCallback(null).free();
			System.exit(-1);
		}
	}

	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
		return System.nanoTime();
	}

	/**
	 * Get the current recorded FPS (frames per second)
	 * 
	 * @return The current FPS
	 */
	public int getFPS() {
		return recordedFPS;
	}

	/**
	 * Retrieve the time taken to render the last frame, i.e. the change in time
	 * - delta.
	 * 
	 * @return The time taken to render the last frame
	 */
	protected float getDelta() {
		long time = getTime();
		float delta = (time - lastFrame) / 1000000000f;
		lastFrame = time;

		return delta;
	}

	/**
	 * Updated the FPS counter
	 */
	protected void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			lastFPS = getTime();
			recordedFPS = fps;
			fps=0;
		}
		fps++;
	}

	/**
	 * Set the minimum amount of time in milliseonds that has to pass before
	 * update() is called on the container game. This gives a way to limit logic
	 * updates compared to renders.
	 * 
	 * @param interval
	 *            The minimum interval between logic updates
	 */
	public void setMinimumLogicUpdateInterval(int interval) {
		minimumLogicInterval = interval;
	}

	/**
	 * Set the maximum amount of time in milliseconds that can passed into the
	 * update method. Useful for collision detection without sweeping.
	 * 
	 * @param interval
	 *            The maximum interval between logic updates
	 */
	public void setMaximumLogicUpdateInterval(int interval) {
		maximumLogicInterval = interval;
	}

	/**
	 * To be called when the game's FRAMEBUFFER is resized. Updates the
	 * PROJECTION matrix.
	 * 
	 * @param framebufferWidth
	 *            The width of the new FRAMEBUFFER
	 * @param framebufferHeight
	 *            The height of the new FRAMEBUFFER
	 */
	public void onResize(int framebufferWidth, int framebufferHeight) {
		FRAMEBUFFER.width = framebufferWidth;
		FRAMEBUFFER.height = framebufferHeight;
		float aspectRatioFB = (float) framebufferWidth / framebufferHeight;
		if(ORTHO){
			PROJECTION.left = -SCENE_WIDTH / 2;
			PROJECTION.right = SCENE_WIDTH / 2;
			PROJECTION.bottom = -SCENE_HEIGHT / 2;
			PROJECTION.top = SCENE_HEIGHT / 2;
			if (aspectRatioFB > ASPECT_RATIO) {
				PROJECTION.left -= (SCENE_HEIGHT * aspectRatioFB - SCENE_WIDTH) / 2f;
				PROJECTION.right += (SCENE_HEIGHT * aspectRatioFB - SCENE_WIDTH) / 2f;
			} else if (aspectRatioFB < ASPECT_RATIO) {
				PROJECTION.bottom -= (SCENE_WIDTH / aspectRatioFB - SCENE_HEIGHT) / 2f;
				PROJECTION.top += (SCENE_WIDTH / aspectRatioFB - SCENE_HEIGHT) / 2f;
			}
			
			setOrtho2D(PROJECTION_MATRIX, PROJECTION);
		}else{
			PROJECTION3D.left = -SCENE_WIDTH / 2;
			PROJECTION3D.right = SCENE_WIDTH / 2;
			PROJECTION3D.bottom = -SCENE_HEIGHT / 2;
			PROJECTION3D.top = SCENE_HEIGHT / 2;
			if (aspectRatioFB > ASPECT_RATIO) {
				PROJECTION3D.left -= (SCENE_HEIGHT * aspectRatioFB - SCENE_WIDTH) / 2f;
				PROJECTION3D.right += (SCENE_HEIGHT * aspectRatioFB - SCENE_WIDTH) / 2f;
			} else if (aspectRatioFB < ASPECT_RATIO) {
				PROJECTION3D.bottom -= (SCENE_WIDTH / aspectRatioFB - SCENE_HEIGHT) / 2f;
				PROJECTION3D.top += (SCENE_WIDTH / aspectRatioFB - SCENE_HEIGHT) / 2f;
			}
			PROJECTION3D.far = 50000;
			PROJECTION3D.near = 0.5f;
			setPerspective2D(PROJECTION_MATRIX, PROJECTION3D);
		}
		//
		glViewport(0, 0, framebufferWidth, framebufferHeight);
	}

	/**
	 * A struct representing a framebuffer.
	 */
	public static class Framebuffer {
		int width, height;
	}

	/**
	 * A struct representing an orthographic PROJECTION.
	 */
	public static class Projection {
		float left, right, bottom, top;
	}
	
	/**
	 * A struct representing an operspective PROJECTION.
	 */
	public static class Projection3D {
		float left, right, bottom, top, far, near;
	}

	/**
	 * Sets the contents of the specified buffer to an orthographic PROJECTION
	 * matrix.
	 * 
	 * @param dest
	 *            The buffer to set.
	 * @param p
	 *            The projection to use.
	 */
	public static void setOrtho2D(Matrix4f dest, Projection p) {
		float f1 = p.right - p.left;
		float f2 = p.top - p.bottom;
		dest.setZero();
		dest.m00 = 2f / f1;
		dest.m11 = 2f / f2;
		dest.m22 = -1;
		dest.m33 = 1;
		dest.m31 = -(p.right + p.left) / f1;
		dest.m32 = -(p.top + p.bottom) / f2;

		// dest.put(new float[]{
		// 2f / f1, 0, 0, 0,
		// 0, 2f / f2, 0, 0,
		// 0, 0, -1, 0,
		// -(p.right + p.left) / f1, -(p.top + p.bottom) / f2, 0, 1
		// });
		// dest.flip();
		PROJECTION_MATRIX_BUFFER.clear();
		dest.store(PROJECTION_MATRIX_BUFFER);
		PROJECTION_MATRIX_BUFFER.flip();
	}
	
	
	/**
	 * Sets the contents of the specified buffer to an orthographic PROJECTION
	 * matrix.
	 * 
	 * @param dest
	 *            The buffer to set.
	 * @param p
	 *            The projection to use.
	 */
	public static void setPerspective2D(Matrix4f dest, Projection3D p) {
		float f1 = p.right - p.left;
		float f2 = p.top - p.bottom;
		dest.setZero();
		dest.m00 = 2*p.near / f1;
		dest.m11 = 2*p.near / f2;
		dest.m22 = -(p.far+p.near)/(p.far-p.near);
		dest.m23 = -2*p.far*p.near/(p.far-p.near);
		dest.m33 = 0;
		dest.m32 = -1;
		dest.m02 = -(p.right + p.left) / f1;
		dest.m12 = -(p.top + p.bottom) / f2;

		// dest.put(new float[]{
		// 2f / f1, 0, 0, 0,
		// 0, 2f / f2, 0, 0,
		// 0, 0, -1, 0,
		// -(p.right + p.left) / f1, -(p.top + p.bottom) / f2, 0, 1
		// });
		// dest.flip();
		PROJECTION_MATRIX_BUFFER.clear();
		dest.store(PROJECTION_MATRIX_BUFFER);
		PROJECTION_MATRIX_BUFFER.flip();
	}
	
	private static void sync(int fps) {
        if (fps <= 0) return;
          
        long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
        long overSleep = 0; // time the sync goes over by
          
        try {
            while (true) {
                long t = System.nanoTime() - lastTime;
                  
                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                }else if (t < sleepTime) {
                    // burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                }else {
                    overSleep = t - sleepTime;
                    break; // exit while loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
             
            // auto tune the time sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
            }
            else if (overSleep < variableYieldTime - 200*1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
            }
        }
    }

	public static void main(String[] args) {
		new GameContainer().run();
	}

}
