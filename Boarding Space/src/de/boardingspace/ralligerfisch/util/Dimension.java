package de.boardingspace.ralligerfisch.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dimension<T extends Number> {
	private List<T> values;
	
	@SafeVarargs
	public Dimension(T... values){
		this.setValues(new ArrayList<T>(Arrays.asList(values)));
	}
	
	public T getValue(int index) throws IndexOutOfBoundsException{
		return values.get(index);
	}

	public void setValues(int index, T value) throws IndexOutOfBoundsException{
		values.set(index, value);
	}
	
	public List<T> getValues() {
		return values;
	}

	public void setValues(List<T> values) {
		this.values = values;
	}
	public Integer length(){
		return values.size();
	}
	public Integer size(){
		return values.size();
	}
	
	public T[] toArray(T[] tmparray){
		return values.toArray(tmparray);
	}
}
