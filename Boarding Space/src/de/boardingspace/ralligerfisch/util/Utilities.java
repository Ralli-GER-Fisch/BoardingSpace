package de.boardingspace.ralligerfisch.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.MonthDay;
//import java.time.OffsetDateTime;
//import java.time.OffsetTime;
//import java.time.Year;
//import java.time.YearMonth;
//import java.time.ZoneId;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
import java.util.HashMap;
//import java.util.function.Function;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;

public class Utilities {
	
	public static Vector3f X3f = new Vector3f(1,0,0);
	public static Vector3f Y3f = new Vector3f(0,1,0);
	public static Vector3f Z3f = new Vector3f(0,0,1);
	

	public static String loadAsString(String location) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(location));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer);
				result.append("\n");
			}
			reader.close();

		} catch (IOException e) {
			System.err.println(e);
		}
		return result.toString();
	}

	public static ByteBuffer createByteBuffer(byte[] array) {
		ByteBuffer result = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
		result.put(array).flip();
		return result;
	}

	public static FloatBuffer createFloatBuffer(float[] array) {
		FloatBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		result.put(array).flip();
		return result;
	}

	public static IntBuffer createIntBuffer(int[] array) {
		IntBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
		result.put(array).flip();
		return result;
	}

	public static <T extends Number, N extends Number> Number[] scaleArray(T[] array, N scalar) {
		Number[] output = new Number[array.length];
		for (int i = 0; i < array.length; i++) {
			output[i] = multiplyNumbers(array[i], scalar);
		}
		return output;
	}
		
	public static Number multiplyNumbers(Number a,Number b){
		if(a instanceof BigDecimal || b instanceof BigDecimal)
			return ((BigDecimal)a).multiply((BigDecimal)b);
		if(a instanceof Double || b instanceof Double)
			return new Double(a.doubleValue()*b.doubleValue());
		if(a instanceof Float || b instanceof Float)
			return new Float(a.floatValue()*b.floatValue());
		if(a instanceof BigInteger || b instanceof BigInteger)
			return ((BigInteger)a).multiply((BigInteger)b);
		if(a instanceof Long || b instanceof Long)
			return new Long(a.longValue()*b.longValue());
		if(a instanceof Integer || b instanceof Integer)
			return new Integer(a.intValue()*b.intValue());
		if(a instanceof Short || b instanceof Short)
			return new Short((short) (a.shortValue()*b.shortValue()));
		if(a instanceof Byte || b instanceof Byte)
			return new Byte((byte) (a.byteValue()*b.byteValue()));
		return null;
	}
	
	public static Number addNumbers(Number a,Number b){
		if(a instanceof BigDecimal || b instanceof BigDecimal)
			return ((BigDecimal)a).add((BigDecimal)b);
		if(a instanceof Double || b instanceof Double)
			return new Double(a.doubleValue()+b.doubleValue());
		if(a instanceof Float || b instanceof Float)
			return new Float(a.floatValue()+b.floatValue());
		if(a instanceof BigInteger || b instanceof BigInteger)
			return ((BigInteger)a).add((BigInteger)b);
		if(a instanceof Long || b instanceof Long)
			return new Long(a.longValue()+b.longValue());
		if(a instanceof Integer || b instanceof Integer)
			return new Integer(a.intValue()+b.intValue());
		if(a instanceof Short || b instanceof Short)
			return new Short((short) (a.shortValue()+b.shortValue()));
		if(a instanceof Byte || b instanceof Byte)
			return new Byte((byte) (a.byteValue()+b.byteValue()));
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> Number dotProduct(T[] A, T[] B) {
		assert A.length == B.length;
		assert A.length > 0;
		Number retval = zeroNumber.get(A[0].getClass());
		for(int i = 0; i<A.length;i++)
			retval = (T) addNumbers(retval,multiplyNumbers(A[i],B[i]));
		return (T) retval;
	}
	
	public static Color lerpColor(Color maxColor, Color minColor, float percentage){
		return new Color(	(int)(minColor.getRed()*percentage+maxColor.getRed()*(1-percentage)),
							(int)(minColor.getGreen()*percentage+maxColor.getGreen()*(1-percentage)),
							(int)(minColor.getBlue()*percentage+maxColor.getBlue()*(1-percentage)));
	}
	
	
	
	private static  HashMap<Class<?>, Number> zeroNumber = new HashMap<>();
	static {
		zeroNumber.put(byte.class          , (byte) 0);
		zeroNumber.put(short.class         , (short) 0);
		zeroNumber.put(int.class           , (int) 0);
		zeroNumber.put(long.class          , (long) 0);
		zeroNumber.put(double.class        , 0d);
		zeroNumber.put(float.class         , 0f);
		zeroNumber.put(Byte.class          , new Byte((byte) 0));
		zeroNumber.put(Short.class         , new Short((short) 0));
		zeroNumber.put(Integer.class       , new Integer((int) 0));
		zeroNumber.put(Long.class          , new Long((long) 0));
		zeroNumber.put(Double.class        , new Double(0d));
		zeroNumber.put(Float.class         , new Float(0f));
		zeroNumber.put(BigDecimal.class    , new BigDecimal(0));
		zeroNumber.put(BigInteger.class    , new BigInteger("0"));
	}
	
}




//private static HashMap<Class<?>, Function<String,?>> parser = new HashMap<>();
//static {
//	parser.put(boolean.class       , Boolean::parseBoolean);
//	parser.put(byte.class          , Byte::parseByte);
//	parser.put(short.class         , Short::parseShort);
//	parser.put(int.class           , Integer::parseInt);
//	parser.put(long.class          , Long::parseLong);
//	parser.put(double.class        , Double::parseDouble);
//	parser.put(float.class         , Float::parseFloat);
//	parser.put(Boolean.class       , Boolean::valueOf);
//	parser.put(Byte.class          , Byte::valueOf);
//	parser.put(Short.class         , Short::valueOf);
//	parser.put(Integer.class       , Integer::valueOf);
//	parser.put(Long.class          , Long::valueOf);
//	parser.put(Double.class        , Double::valueOf);
//	parser.put(Float.class         , Float::valueOf);
//	parser.put(String.class        , String::valueOf);
//	parser.put(BigDecimal.class    , BigDecimal::new);
//	parser.put(BigInteger.class    , BigInteger::new);
//	parser.put(LocalDate.class     , LocalDate::parse);
//	parser.put(LocalDateTime.class , LocalDateTime::parse);
//	parser.put(LocalTime.class     , LocalTime::parse);
//	parser.put(MonthDay.class      , MonthDay::parse);
//	parser.put(OffsetDateTime.class, OffsetDateTime::parse);
//	parser.put(OffsetTime.class    , OffsetTime::parse);
//	parser.put(Year.class          , Year::parse);
//	parser.put(YearMonth.class     , YearMonth::parse);
//	parser.put(ZonedDateTime.class , ZonedDateTime::parse);
//	parser.put(ZoneId.class        , ZoneId::of);
//	parser.put(ZoneOffset.class    , ZoneOffset::of);
//}