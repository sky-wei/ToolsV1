package com.wei.bean;

import java.io.DataOutputStream;
import java.io.IOException;

public class SearchItem {
	
	private int key;
	private String value;
	
	public SearchItem(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public void writeSearchItem(DataOutputStream dos) throws IOException {
		dos.writeInt(key);
		dos.writeUTF(value);
	}

	@Override
	public String toString() {
		return key + " --> \"" + value + "\"";
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + value.hashCode();
		result = 37 * result + key;
		return result;
	}
}
