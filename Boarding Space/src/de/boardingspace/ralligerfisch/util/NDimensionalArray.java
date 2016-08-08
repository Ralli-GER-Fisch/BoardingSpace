package de.boardingspace.ralligerfisch.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;

public class NDimensionalArray<T> {
	private List<T> array;
	private Integer[] dimensions;
	private Integer[] multipliers;
	private Integer size = 0;
	
	public NDimensionalArray(Integer... dimensions) {
		int arraySize = 1;

		multipliers = new Integer[dimensions.length];
		for (int idx = dimensions.length - 1; idx >= 0; idx--) {
			multipliers[idx] = arraySize;
			arraySize *= dimensions[idx];
		}
		size = arraySize;
		array = new ArrayList<T>(arraySize);
		this.dimensions = dimensions;
	}
	
	public NDimensionalArray(Dimension<Integer> dimension) {
		int arraySize = 1;

		multipliers = new Integer[dimension.length()];
		for (int idx = dimension.length() - 1; idx >= 0; idx--) {
			multipliers[idx] = arraySize;
			arraySize *= dimension.getValue(idx);
		}
		size = arraySize;
		array = new ArrayList<T>(arraySize);
		this.dimensions = dimension.toArray(new Integer[dimension.length()]);
	}
	

	public T get(Integer... indices) {
		assert indices.length == dimensions.length;
		int internalIndex = 0;

		for (Integer idx = 0; idx < indices.length; idx++) {
			internalIndex += indices[idx] * multipliers[idx];
		}
		return array.get(internalIndex);
	}
	
	public void set(T value, Integer... indices) {
		assert indices.length == dimensions.length;
		Integer internalIndex = 0;
		for (Integer idx = 0; idx < indices.length; idx++) {
			internalIndex += indices[idx] * multipliers[idx];
		}
		array.set(internalIndex,value);
	}
	
	public void setLinearIndex(T value, Integer linearIndex) {
		assert linearIndex <= getLinearSize();
		array.add(linearIndex.intValue(),value);
		//array.set(linearIndex.intValue(),value);
	}
	
	public Integer getLinearSize(){
		return size;
	}
	
	public Integer[] linearIndexToCoordinates(Integer linearIndex){
		Integer[] retval = new Integer[dimensions.length];
		for(Integer i = 0; i < dimensions.length;i++){
			retval[i] = linearIndex % (multipliers[i]+1);
			linearIndex /= (multipliers[i]+1);
		}
		return retval;
	}
	
	public ByteBuffer toByteArray(){
		ByteBuffer retval = ByteBuffer.allocate(array.size());
		Iterator<T> iter = array.iterator();
		while(iter.hasNext()){
			retval.put((new Double(iter.next().toString())).byteValue());
		}
		return retval;
	}

	public Integer getDimension(Integer i) {
		return dimensions[i];
	}

}
