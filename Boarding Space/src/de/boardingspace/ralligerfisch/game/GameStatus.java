package de.boardingspace.ralligerfisch.game;

//import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Renderable;
import org.lwjgl.util.vector.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;

import de.boardingspace.ralligerfisch.game.GameContainer.Framebuffer;
import de.boardingspace.ralligerfisch.game.GameContainer.Projection;
import de.boardingspace.ralligerfisch.game.GameContainer.Projection3D;
import de.boardingspace.ralligerfisch.gameobject.charakter.Player;
import de.boardingspace.ralligerfisch.util.Updateable;

public class GameStatus {
	/** VARIABLES */
//	public static int 					KEY_UP = -1;
//	public static int 					KEY_DOWN = -1;
//	public static int 					KEY_LEFT = -1;
//	public static int 					KEY_RIGHT = -1;
	/** KEY VARIABLES */
	public static HashMap<Integer, Boolean> KEY_MAP = new HashMap<>(16);
	static {
			KEY_MAP.put(GLFW_KEY_UP, false);
			KEY_MAP.put(GLFW_KEY_DOWN, false);
			KEY_MAP.put(GLFW_KEY_RIGHT, false);
			KEY_MAP.put(GLFW_KEY_LEFT, false);
			KEY_MAP.put(GLFW_KEY_W, false);
			KEY_MAP.put(GLFW_KEY_A, false);
			KEY_MAP.put(GLFW_KEY_S, false);
			KEY_MAP.put(GLFW_KEY_D, false);
	}
	
	/** MOUSE VARIABLES */
	protected static double 			CURSOR_X_POS = 0d;
	protected static double 			CURSOR_Y_POS = 0d;
	protected static double 			CURSOR_X_SCROLL_OFFSET = 0d;
	protected static double 			CURSOR_X_SCROLL_OFFSET_TMP = 0d;
	protected static double 			CURSOR_Y_SCROLL_OFFSET = 0d;
	protected static double				CURSOR_Y_SCROLL_OFFSET_TMP = 0d;
	protected static int				MOUSE_BUTTON = -1;
	protected static float				SCENE_WIDTH,
										SCENE_HEIGHT = 1f;
	protected static Player				MAIN_PLAYER;
	

	/** DISPLAY VAIRABLES */

	/**
	 * Wrapper for the framebuffer dimensions. For transforming mouse click
	 * coords.
	 */
	protected static final Framebuffer	FRAMEBUFFER = new Framebuffer();
	/**
	 * Wrapper for the orthographic projection currently used. For transforming
	 * mouse click coords.
	 */
	protected static final Projection	PROJECTION = new Projection();
	protected static final Projection3D	PROJECTION3D = new Projection3D();
	protected static final boolean		ORTHO = false;
	/**
	 * The location and a buffer representing the projectionMatrix uniform.
	 */
	protected static int 				PROJECTION_MATRIX_Loc;
	protected static Matrix4f	 		PROJECTION_MATRIX = new Matrix4f();
	protected static FloatBuffer		PROJECTION_MATRIX_BUFFER = BufferUtils.createFloatBuffer(16);
	/**
	 * The location and a buffer representing the viewMatrix uniform.
	 */
	protected static int 				VIEW_MATRIX_Loc;
	protected static Matrix4f			VIEW_MATRIX = new Matrix4f();
	protected static FloatBuffer		VIEW_MATRIX_BUFFER = BufferUtils.createFloatBuffer(16);
	/**
	 * The location and a buffer representing the modelMatrix uniform.
	 */
	public static int 					MODEL_MATRIX_Loc;
	

	/** OPENGL VARIABLES/POINTERS */
	/**
	 * The current zoom_value.
	 */
	protected static float 				ZOOM_VALUE = 0.5f;
	/**
	 * SHADER IDs
	 */
	protected static int 				ID_SHADER_VERTEX = 0;
	protected static int 				ID_SHADER_FRAGMENT = 0;
	protected static int 				ID_SHADER_PROGRAM = 0;
	protected static String 			NAME_SHADER_PROGRAM = "";

	/** CONFIGURATION */
	protected static float 				FAC_ZOOM = 0.1f;
	protected static float 				ZOOM_MIN = -10f;
	protected static float 				ZOOM_MAX = 0.5f;
	/**
	 * The initial width and height of the window.
	 */
	protected static int 				WINDOW_WIDTH = 1600,
										WINDOW_HEIGHT = 900;
	/**
	 * The initial display aspect ratio in game.
	 */
	protected static float 				ASPECT_RATIO = 16f / 9f;

	/** ALL RENDERABLE OBJECTs*/
	protected static ArrayList<Renderable> 	RENDERABLE_OBJECTS = new ArrayList<>();
	protected static ArrayList<Updateable> 	UPDATEABLE_OBJECTS = new ArrayList<>();
	
	
	/** DEBUG VAIRABLE */
	public static boolean DEBUG = true;
	
}
