package de.boardingspace.ralligerfisch.gameobject.outfit;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.util.vector.Vector3f;



public class Thruster extends Outfit {
	private Vector3f relativePosition = new Vector3f();
	private float theta = 0f;
	private Vector3f relativeOrientation = new Vector3f(-1f, 0f, 0f);

	/** CONFIGURABLE PARAMETERS */
	private float UNTHRUST_FACTOR = 0.5f;
	private float THRUST_THRESHOLD = 0.5f;
	private float THRUST_MAX = 1250000;
	private HashMap<Integer, Consumer<Float>> FUNKTION_LIST = new HashMap<>();
	private HashMap<Integer, Integer> KEY_FUNCTION_MAP = new HashMap<>();
	private HashMap<Integer, String> FUNCTION_NAMES = new HashMap<>();

	/** UPDATABLE PARAMETERS */
	private float CURRENT_THRUST_VALUE = 0;

	private boolean needToRelaxThrust = false;
	
	/** UTIL VARIABLES */
	private Vector3f UTIL_THRUST = new Vector3f();
	private Vector3f UTIL_TORQUE = new Vector3f();

	public Thruster(Vector3f relativePosition, float relativeRotationTheta) {
		this.relativePosition.set(relativePosition);
		this.theta = relativeRotationTheta;
		relativeOrientation.set((float) Math.cos(theta), (float) Math.sin(theta), 0);// Assuming
																						// only
																						// rotation
																						// around
																						// x
																						// axis
		setUpdateobject(new UpdateObject(UpdateObject.THRUSTER));
		fillLists();
	}
	
	public void assignKeyPair(int keyIncrease, int keyDecrease){
		
	}

	@Override
	public void update(float delta) {
		UTIL_THRUST.set(relativeOrientation);
		UTIL_THRUST.scale(CURRENT_THRUST_VALUE);
		Vector3f.cross(relativePosition, relativeOrientation, UTIL_TORQUE);
		UTIL_TORQUE.scale(CURRENT_THRUST_VALUE);
		getUpdateobject().thrust = UTIL_THRUST;
		getUpdateobject().torque = UTIL_TORQUE;
		relaxEngines(delta);
	}

	public void increaseThrust(float delta) {
		CURRENT_THRUST_VALUE += THRUST_MAX * delta;
		CURRENT_THRUST_VALUE = CURRENT_THRUST_VALUE > THRUST_MAX ? THRUST_MAX : CURRENT_THRUST_VALUE;
		needToRelaxThrust = false;
	}

	public void decreaseThrust(float delta) {
		CURRENT_THRUST_VALUE -= THRUST_MAX * delta;
		CURRENT_THRUST_VALUE = CURRENT_THRUST_VALUE < 0 ? 0 : CURRENT_THRUST_VALUE;
		needToRelaxThrust = false;
	}

	public void relaxThrust(float delta) {
		if (CURRENT_THRUST_VALUE > 0) {
			CURRENT_THRUST_VALUE -= THRUST_MAX * delta * UNTHRUST_FACTOR;
			CURRENT_THRUST_VALUE = CURRENT_THRUST_VALUE < THRUST_THRESHOLD ? 0 : CURRENT_THRUST_VALUE;
		}
	}

	public void startRelaxing() {
		needToRelaxThrust = true;
	}

	public void relaxEngines(float delta) {
		if (needToRelaxThrust)
			relaxThrust(delta);
		startRelaxing();
	}

	public Class<?> getType() {
		return Thruster.class;
	}
	
	private void fillLists(){ /** Maybe read from cfg file */
		FUNCTION_NAMES.put(0, "Increase Thrust");
		FUNCTION_NAMES.put(1, "Decrease Thrust");
		FUNKTION_LIST.put(0, (Consumer<Float>) this::increaseThrust);
		FUNKTION_LIST.put(1, (Consumer<Float>) this::decreaseThrust);
		KEY_FUNCTION_MAP.put(GLFW_KEY_W, 0);
		KEY_FUNCTION_MAP.put(GLFW_KEY_A, 1);
	}
	
	public HashMap<Integer, Consumer<Float>> getFunktionList(){
		return FUNKTION_LIST;
	}

	public HashMap<Integer, String> getFunktionIdNames() {
		return FUNCTION_NAMES;
	}

	public HashMap<Integer, Integer> getKeyFunctionMap() {
		return KEY_FUNCTION_MAP;
	}

}
